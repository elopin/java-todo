/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.iivos.todo.security;

import cz.iivos.todo.model.User;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author JS
 */
public class SecurityServiceTest {
    
    static SecurityService instance;
    
    public SecurityServiceTest() {
    }
    
    @BeforeClass
    public static void init() {
        instance = new SecurityService();
    }
    
    @BeforeClass
    public static void init2() {
        instance = new SecurityService();
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of logout method, of class SecurityService.
     */
    @Test
    public void testLogout() {
        System.out.println("logout");
        SecurityService instance = new SecurityService();
        instance.logout();
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getCurrentUser method, of class SecurityService.
     */
    @Test
    public void testGetCurrentUser() {
        System.out.println("getCurrentUser");
        SecurityService instance = new SecurityService();
        User expResult = null;
        //User result = instance.getCurrentUser();
        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of matchPassword method, of class SecurityService.
     */
    @Test
    public void testMatchPassword() {
        System.out.println("matchPassword");
        String rawPassword = "";
        String hashPassword = "";
        SecurityService instance = new SecurityService();
        boolean expResult = false;
        //boolean result = instance.matchPassword(rawPassword, hashPassword);
        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getEncryptedPassword method, of class SecurityService.
     */
    @Test
    public void testGetEncryptedPassword() {
        System.out.println("getEncryptedPassword");
        String password = "";
        SecurityService instance = new SecurityService();
        String expResult = "";
        //String result = instance.getEncryptedPassword(password);
        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
}
