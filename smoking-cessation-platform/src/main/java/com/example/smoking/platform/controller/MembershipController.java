package com.example.smoking.platform.controller;

import com.example.smoking.platform.model.MembershipPackage;
import com.example.smoking.platform.repository.MembershipPackageRepository;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MembershipController {

    @Autowired
    private MembershipPackageRepository membershipPackageRepository;

    @PostConstruct
    public void initMembershipPackages() {
        if (membershipPackageRepository.count() == 0) {
            membershipPackageRepository.save(new MembershipPackage("Gói Cơ Bản", "Truy cập tính năng theo dõi cơ bản.", 0.0));
            membershipPackageRepository.save(new MembershipPackage("Gói Nâng Cao", "Thêm huy hiệu, kế hoạch chi tiết, hỗ trợ cộng đồng.", 99000.0));
            membershipPackageRepository.save(new MembershipPackage("Gói Huấn Luyện Viên", "Tư vấn 1-1 với HLV + tất cả tính năng nâng cao.", 199000.0));
        }
    }


    @GetMapping("/membership")
    public String showMembershipPage(Model model) {
        System.out.println(">>> Đã vào Membership Controller");

        List<MembershipPackage> packages = membershipPackageRepository.findAll();
        model.addAttribute("packages", packages);
        return "membership";
    }
}
