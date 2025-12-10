package com.shortclip.backend.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class AzureBlobService {

    private final BlobContainerClient containerClient;

    public AzureBlobService(
            @Value("${azure.storage.connection-string}") String connectionString,
            @Value("${azure.storage.container-name}") String containerName) {

        BlobServiceClient serviceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        this.containerClient = serviceClient.getBlobContainerClient(containerName);
        if (!this.containerClient.exists()) {
            this.containerClient.create();
        }
    }

    /**
     * Uploads a file to the configured Azure Blob container and
     * returns the public blob URL.
     */
    public String upload(MultipartFile file) throws IOException {
        String blobName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        BlobClient blobClient = containerClient.getBlobClient(blobName);

        try (InputStream is = file.getInputStream()) {
            blobClient.upload(is, file.getSize(), true);
        }

        // Set correct content-type (so video plays correctly in browser)
        BlobHttpHeaders headers = new BlobHttpHeaders()
                .setContentType(file.getContentType());
        blobClient.setHttpHeaders(headers);

        return blobClient.getBlobUrl();
    }
}
