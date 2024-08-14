package com.nt.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.nt.bindings.CitizenAppRegistrationInputs;
import com.nt.entity.CitizenAppRegistrationEntity;
import com.nt.exceptions.InvalidSSNException;
import com.nt.repository.IApplicationRegistrationRepository;

import reactor.core.publisher.Mono;

@Service
public class CitizenAppRegistrationServiceImpl implements ICitizenAppRegistrationService {
	@Autowired
	private IApplicationRegistrationRepository citizenRepo;
	/*@Autowired
	private RestTemplate template;
	*/
	@Autowired
	private WebClient client;
	@Value("${ar.ssa-web.url}")
	private String endPointURL;
	@Value("${ar.state}")
	private String targetState;

	@Override
	public Integer registerCitizenApp(CitizenAppRegistrationInputs inputs)throws InvalidSSNException{
		
		/*
		//perform  web service call  to check weather  SSN is valid or not  and get them state name.
		ResponseEntity<String>response=template.exchange(endPointURL, HttpMethod.GET,null,String.class,inputs.getSsn());
		*/
		//perform  web service call  to check weather  SSN is valid or not  and get them state name. Using WebClient
		//get State name
		
		Mono<String>response=client.get().uri(endPointURL,inputs.getSsn()).retrieve()
				                                         .onStatus(HttpStatus.BAD_REQUEST::equals, res->res.bodyToMono(String.class).map(ex->new InvalidSSNException("Invalid SSN"))).bodyToMono(String.class);
		String stateName=response.block();
		//register the citizen if he belongs to California state 
		if(stateName.equalsIgnoreCase(targetState)) {
			//prepare the entity class
			CitizenAppRegistrationEntity entity=new CitizenAppRegistrationEntity();
			BeanUtils.copyProperties(inputs, entity);
			entity.setStateName(stateName);
			//save the object
			int appId=citizenRepo.save(entity).getAppId();
			return appId;
		}
		
		throw new InvalidSSNException("Invalid SSN");
	}

}
