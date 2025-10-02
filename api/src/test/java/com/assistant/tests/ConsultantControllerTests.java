package com.assistant.tests;

import com.assistant.controllers.ConsultantController;
import com.assistant.repositories.ConsultantRepository;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ConsultantController.class)
public class ConsultantControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ConsultantRepository repository;

//    @Test
//    public void givenCountMethodMocked_WhenCountInvoked_ThenMockValueReturned() {
//        Mockito.when(mockRepository.count()).thenReturn(123L);
//
//        long userCount = mockRepository.count();
//
//        Assert.assertEquals(123L, userCount);
//        Mockito.verify(mockRepository).count();
//    }
    @Test
    public void

}
