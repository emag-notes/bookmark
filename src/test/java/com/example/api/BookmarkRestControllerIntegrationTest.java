package com.example.api;

import com.example.App;
import com.example.domain.Bookmark;
import com.example.repository.BookmarkRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@WebAppConfiguration
@IntegrationTest({
  "server.port:0",
  "spring.datasource.url:jdbc:h2:mem:bookmark;DB_CLOSE_ON_EXIT=FALSE"
})
public class BookmarkRestControllerIntegrationTest {

  @Autowired
  BookmarkRepository bookmarkRepository;
  @Value("${local.server.port}")
  int port;
  String apiEndPoint;
  RestTemplate restTemplate = new TestRestTemplate();
  Bookmark springIO;
  Bookmark springBoot;

  @Before
  public void setUp() throws Exception {
    bookmarkRepository.deleteAll();

    springIO = new Bookmark("Spring IO", "http://spring.io");
    springBoot = new Bookmark("Spring Boot", "http://projects.spring.io/spring-boot");
    bookmarkRepository.save(Arrays.asList(springIO, springBoot));

    apiEndPoint = "http://localhost:" + port + "/api/bookmarks";
  }

  @Test
  public void testGetBookmark() throws Exception {
    ResponseEntity<Bookmark> response = restTemplate.exchange(apiEndPoint + "/{id}",
                                      HttpMethod.GET, null, Bookmark.class, Collections.singletonMap("id", springIO.getId()));
    assertThat(response.getStatusCode(), is(HttpStatus.OK));

    Bookmark bookmark = response.getBody();
    assertThat(bookmark.getId(), is(springIO.getId()));
    assertThat(bookmark.getName(), is(springIO.getName()));
    assertThat(bookmark.getUrl(), is(springIO.getUrl()));
  }

  @Test
  public void testGetBookmarks() throws Exception {
    ResponseEntity<List<Bookmark>> response = restTemplate.exchange(apiEndPoint,
                                                HttpMethod.GET, null, new ParameterizedTypeReference<List<Bookmark>>() {});
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat(response.getBody().size(), is(2));

    Bookmark bookmark1 = response.getBody().get(0);
    assertThat(bookmark1.getId(), is(springIO.getId()));
    assertThat(bookmark1.getName(), is(springIO.getName()));
    assertThat(bookmark1.getUrl(), is(springIO.getUrl()));

    Bookmark bookmark2 = response.getBody().get(1);
    assertThat(bookmark2.getId(), is(springBoot.getId()));
    assertThat(bookmark2.getName(), is(springBoot.getName()));
    assertThat(bookmark2.getUrl(), is(springBoot.getUrl()));
  }
  
  @Test
  public void testPostBookmark() throws Exception {
    Bookmark google = new Bookmark("Google", "http://google.com");

    ResponseEntity<Bookmark> response = restTemplate.exchange(apiEndPoint,
                                          HttpMethod.POST, new HttpEntity<>(google), Bookmark.class);
    assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
    Bookmark bookmark = response.getBody();
    assertThat(bookmark.getId(), is(notNullValue()));
    assertThat(bookmark.getName(), is(google.getName()));
    assertThat(bookmark.getUrl(), is(google.getUrl()));
    
    assertThat(restTemplate.exchange(apiEndPoint,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Bookmark>>() {})
                .getBody().size(),
              is(3));
  }

  @Test
  public void testDeleteBookmark() throws Exception {
    ResponseEntity<Void> response = restTemplate.exchange(apiEndPoint + "/{id}",
                                      HttpMethod.DELETE, null, Void.class, Collections.singletonMap("id", springIO.getId()));
    assertThat(response.getStatusCode(), is(HttpStatus.NO_CONTENT));

    assertThat(restTemplate.exchange(apiEndPoint,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Bookmark>>() {})
                .getBody().size(),
              is(1));
  }
}