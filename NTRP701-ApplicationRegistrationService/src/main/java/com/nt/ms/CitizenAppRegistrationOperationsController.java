package com.nt.ms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nt.bindings.CitizenAppRegistrationInputs;
import com.nt.service.ICitizenAppRegistrationService;

@RestController
@RequestMapping("/CitizenAR-api")
public class CitizenAppRegistrationOperationsController {
	
	@Autowired
	private ICitizenAppRegistrationService  registerService;
	
	@PostMapping("/save")
	public ResponseEntity<String> saveCitizenApp(@RequestBody CitizenAppRegistrationInputs inputs) {
		try {
			//use service
			int appId=registerService.registerCitizenApp(inputs);
			if(appId>0)
				return new ResponseEntity<String>("The Citizen Application Registered with the ID Value is  ::::::"+appId,HttpStatus.CREATED);
			else
				return new ResponseEntity<String>("Invalid SSN or citizen must belongs to California State  :: ",HttpStatus.OK);
		}
		catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

}
