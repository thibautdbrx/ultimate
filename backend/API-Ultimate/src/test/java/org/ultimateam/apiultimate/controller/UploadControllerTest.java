

package org.ultimateam.apiultimate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.ultimateam.apiultimate.service.StorageService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UploadControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private UploadController uploadController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(uploadController).build();
    }

    @Test
    void upload_ShouldReturnFilenameAndUrl() throws Exception {
        // Simulation d'un fichier multipart
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "contenu de l'image".getBytes()
        );

        when(storageService.store(any())).thenReturn("test.jpg");

        mockMvc.perform(multipart("/api/files/upload").file(mockFile))
                .andExpect(status().isCreated()) // Vérifie le code 201
                .andExpect(jsonPath("$.filename").value("test.jpg"))
                .andExpect(jsonPath("$.url").value("/api/files/test.jpg"));
    }

    @Test
    void serve_ShouldReturnResource() throws Exception {
        String filename = "test.jpg";
        byte[] content = "données binaires".getBytes();
        Resource mockResource = new ByteArrayResource(content) {
            @Override
            public String getFilename() {
                return filename;
            }
        };

        when(storageService.loadAsResource(filename)).thenReturn(mockResource);

        mockMvc.perform(get("/api/files/{filename}", filename))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"test.jpg\""))
                .andExpect(content().bytes(content));
    }
}