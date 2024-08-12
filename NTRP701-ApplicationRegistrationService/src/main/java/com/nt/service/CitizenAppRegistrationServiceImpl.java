package com.nt.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.nt.bindings.CitizenAppRegistrationInputs;
import com.nt.entity.CitizenAppRegistrationEntity;
import com.nt.repository.IApplicationRegistrationRepository;

@Service
public class CitizenAppRegistrationServiceImpl implements ICitizenAppRegistrationService {
	@Autowired
	private IApplicationRegistrationRepository citizenRepo;
	@Autowired
	private RestTemplate template;
	@Value("${ar.ssa-web.url}")
	private String endPointURL;
	@Value("${ar.state}")
	private String targetState;

	@Override
	public Integer registerCitizenApp(CitizenAppRegistrationInputs inputs) {
		
		//perform  web service call  to check weather  SSN is valid or not  and get them state name.
		ResponseEntity<String>response=template.exchange(endPointURL, HttpMethod.GET,null,String.class,inputs.getSsn());
		//get State name
		String stateName=response.getBody();
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
		
		return 0;
	}

}
