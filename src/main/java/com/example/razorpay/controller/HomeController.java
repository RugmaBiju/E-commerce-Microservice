package com.example.razorpay.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Redirects the root URL to the checkout page.
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/payment/checkout";
    }
}
