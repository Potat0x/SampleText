package com.example.demo.api;

import com.example.demo.DemoApplicationTests;
import com.example.demo.domain.ProductFacade;
import com.example.demo.domain.ProductRequestDto;
import com.example.demo.domain.ProductResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.net.URISyntaxException;

public class ProductEndpointTest extends DemoApplicationTests {

    @Autowired
    ProductFacade productFacade;

    private String mapToJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpEntity<String> getHttpRequest(String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        return new HttpEntity<>(json, headers);
    }

    @Test
    public void shouldCreateProduct() throws URISyntaxException {
        //given
        final String url = "http://localhost:" + port + "/products/add";
        //when
        ProductRequestDto requestDto = new ProductRequestDto("testname");
        String requestJson = mapToJson(requestDto);
        ResponseEntity<ProductResponseDto> response = httpClient.postForEntity(url, getHttpRequest(requestJson), ProductResponseDto.class);
        //then
        Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(200);//201 created
        ProductResponseDto rdto = response.getBody();
        Assertions.assertThat(rdto.getName()).isEqualTo("testname");
    }

    @Test
    public void shouldGetExistingProduct() {
        //given
        ProductRequestDto requestDto = new ProductRequestDto("name2");
        ProductResponseDto existingProduct = productFacade.create(requestDto);
        final String url = "http://localhost:" + port + "/products/" + existingProduct.getId();
        //when
        ResponseEntity<ProductResponseDto> result = httpClient.getForEntity(url, ProductResponseDto.class);
        //then
        Assertions.assertThat(result.getStatusCodeValue()).isEqualTo(200);
        Assertions.assertThat(result.getBody()).isEqualToComparingFieldByField(existingProduct);
    }

    @Test
    public void shouldGet404() {
        //given
        final String url = "http://localhost:" + port + "/products/nonexistentid";
        //when
        ResponseEntity<ProductResponseDto> result = httpClient.getForEntity(url, ProductResponseDto.class);
        //then
        Assertions.assertThat(result.getStatusCodeValue()).isEqualTo(404);
    }
}