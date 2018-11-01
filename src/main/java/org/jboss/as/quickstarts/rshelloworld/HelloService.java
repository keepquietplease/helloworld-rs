/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.rshelloworld;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * A simple CDI service which is able to say hello to someone
 *
 * @author Pete Muir
 */
@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Lock(LockType.READ)
public class HelloService {
	
	/**
	 * Play with enum
	 */
	public enum KieServerRuntimeMode {
		OCP("openshift"), K8S("kubernetes"), CLASSIC("classic");
		
		// Defines system property name
		public static final String KIE_SERVER_RUNTIME_MODE = "org.kie.server.runtime.mode";
		
		private final String mode;
		
		KieServerRuntimeMode(String mode) {
			this.mode = mode;
		}
		
		@Override
		public String toString() {
			return mode;
		}
	}
	
	// Test system property constants
	public static final String KIE_SERVER_RUNTIME_MODE = KieServerRuntimeMode.KIE_SERVER_RUNTIME_MODE;
	
	private String nickname;
	
	private class Parent {
		
		private String nickename = "ParentNickName";
		
		public String toString() {
			return "This is Parent" + this.nickename;
		}
	}
	
	private class Child extends Parent {
		
		private String nickname = "ChildNickName";
		
		@Override
		public String toString() {
			return "This is Child -->" + HelloService.this.nickname;
		}
	}
	
	@PostConstruct
	public void setDefaultNickname() {
		this.nickname = "Magic Mike";
	}

	@Lock(LockType.WRITE)
    public String createHelloMessage(String name) {
		
		Child c = this.new Child();
		Parent p = c;
		
		// Test enum with switch
		KieServerRuntimeMode kMode = KieServerRuntimeMode.OCP;
		switch (kMode) {
		case CLASSIC:
			p.nickename = KieServerRuntimeMode.CLASSIC.toString();
			break;
		case K8S:
			p.nickename = KieServerRuntimeMode.K8S.toString();
			break;
		case OCP:
			p.nickename = KieServerRuntimeMode.OCP.toString();
		default:
			p.nickename = KieServerRuntimeMode.CLASSIC.toString();
			break;
		}
		
        return "Hello " 
        	+ this.nickname 
        	+ ':' 
        	+ name 
        	+ " from " 
        	+ p 
        	+ " ! "
        	+ p.nickename
        	+ " "
        	+ KIE_SERVER_RUNTIME_MODE;	// Should print out  'This is Parent'
    }

}
