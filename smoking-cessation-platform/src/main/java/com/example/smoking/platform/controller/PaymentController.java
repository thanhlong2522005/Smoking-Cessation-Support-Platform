package com.example.smoking.platform.controller;

import com.example.smoking.platform.model.MembershipPackage;
import com.example.smoking.platform.model.User;
import com.example.smoking.platform.repository.MembershipPackageRepository;
import com.example.smoking.platform.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; 
import org.springframework.web.bind.annotation.*;

@Controller
public class PaymentController {

    @Autowired
    private MembershipPackageRepository membershipPackageRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/payment/init")
    public String initPayment(@RequestParam("packageId") Long packageId, Model model) {
        System.out.println(">>> Nhận gói ID = " + packageId); // test log
        MembershipPackage pkg = membershipPackageRepository.findById(packageId).orElse(null);
        if (pkg == null) return "redirect:/membership";
        model.addAttribute("selectedPackage", pkg);
        return "payment-confirm";
    }

    @PostMapping("/payment/confirm")
    public String processPayment(@RequestParam("packageId") Long packageId,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return "redirect:/login";
        User user = userRepository.findByEmail(userDetails.getUsername());
        MembershipPackage membershipPackage = membershipPackageRepository.findById(packageId).orElse(null);
        if (user != null && membershipPackage != null) {
            user.setMembershipPackage(membershipPackage);
            userRepository.save(user);
        }
        return "redirect:/payment-success";
    }

    @GetMapping("/payment-success")
    public String paymentSuccessPage() {
        return "payment-success";
    }
}
