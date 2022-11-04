//package com.nanum.userservice;
//
//import com.nanum.userservice.user.presentation.UserController;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@RunWith(SpringRunner.class)
//@WebMvcTest(controllers = UserController.class)
//public class UserControllerTest {
//
//    @Autowired
//    private MockMvc mvc;
//
//    @Test
//    public void 이메일리턴() throws Exception {
//
//        mvc.perform(MockMvcRequestBuilders.get("/api/v1/check/email/{email}")
//                        .param("email","spharos@gmail.com"))
//                .andExpect(status().isNoContent());
//    }
//
//}
