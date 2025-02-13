package com.example.elice_3rd.counsel.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.elice_3rd.counsel.entity.Counsel;
import com.example.elice_3rd.counsel.repository.CounselRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class counselService {
	
	private final CounselRepository counselRepository;
	
	public List<Counsel> getAllCounsels(){
		return counselRepository.findAll();
	}
	
	public Counsel createCounsel(Counsel counsel) {
		return counselRepository.save(counsel);
	}
	
}
