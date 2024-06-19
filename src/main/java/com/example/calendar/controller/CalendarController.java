package com.example.calendar.controller;

import com.example.calendar.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 캘린더 추가하기 API를 받을 수 있는 controller와 메서드 생성
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CalendarController {
    private final CalendarService calendarService;

    @PostMapping("/calendar")
    public boolean createCalendar(){
        calendarService.renewToken();
        return calendarService.createCalendar();
    }
}
