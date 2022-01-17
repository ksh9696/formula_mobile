package com.example.inzent.service;

import com.example.inzent.dao.TestDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService{
    private final TestDAO testDao;
}
