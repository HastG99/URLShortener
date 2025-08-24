package ru.hastg99.urlshortener.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.hastg99.urlshortener.model.Link;
import ru.hastg99.urlshortener.service.LinkService;
import ru.hastg99.urlshortener.service.VisitorService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LinkController.class)
class LinkControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LinkService linkService;

    @MockitoBean
    private VisitorService visitorService;

    @Test
    void showForm_ReturnsIndexPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void createLink_ValidUrl_ReturnsShortUrl() throws Exception {
        when(linkService.createShortLink(any(), any()))
                .thenReturn("abc123");

        mockMvc.perform(post("/create")
                        .param("originalUrl", "https://example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("shortUrl", "message"))
                .andExpect(model().attribute("shortUrl", "http://localhost:8080/abc123"));
    }

    @Test
    void redirect_ValidCode_RedirectsToOriginalUrl() throws Exception {
        Link link = new Link("https://example.com", "abc123");
        link.setExpiresAt(LocalDateTime.now().plusDays(1));

        when(linkService.findByShortCode("abc123")).thenReturn(link);
        when(linkService.getOriginalUrl("abc123")).thenReturn("https://example.com");

        mockMvc.perform(get("/abc123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("https://example.com"));
    }

    @Test
    void showStats_ValidCode_ReturnsStatsPage() throws Exception {
        Link link = new Link("https://example.com", "abc123");
        link.setExpiresAt(LocalDateTime.now().plusDays(1));

        when(linkService.findByShortCode("abc123")).thenReturn(link);

        mockMvc.perform(get("/stats/abc123"))
                .andExpect(status().isOk())
                .andExpect(view().name("stats"))
                .andExpect(model().attributeExists("link", "recentVisitors", "visitorCount"));
    }
}