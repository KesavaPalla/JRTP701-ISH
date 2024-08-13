package com.nt.service;

import com.nt.bindings.CitizenAppRegistrationInputs;

public interface ICitizenAppRegistrationService {
	public Integer registerCitizenApp(CitizenAppRegistrationInputs inputs)throws Exception;

}
