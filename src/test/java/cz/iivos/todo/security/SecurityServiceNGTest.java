/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.iivos.todo.security;

import cz.iivos.todo.model.User;
import static org.junit.Assert.*;
import static org.testng.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author JS
 */
public class SecurityServiceNGTest {
    
    public SecurityServiceNGTest() {
    }

    @org.testng.annotations.BeforeClass
    public static void setUpClass() throws Exception {
    }

    @org.testng.annotations.AfterClass
    public static void tearDownClass() throws Exception {
    }

    @org.testng.annotations.BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @org.testng.annotations.AfterMethod
    public void tearDownMethod() throws Exception {
    }

    /**
     * Test of logout method, of class SecurityService.
     */
    @org.testng.annotations.Test
    public void testLogout() {
        System.out.println("logout");
        //SecurityService instance = new SecurityService();
        //instance.logout();
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getCurrentUser method, of class SecurityService.
     */
    @org.testng.annotations.Test
    public void testGetCurrentUser() {
        System.out.println("getCurrentUser");
        //SecurityService instance = new SecurityService();
        User expResult = null;
        //User result = instance.getCurrentUser();
        //assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of matchPassword method, of class SecurityService.
     */
    @org.testng.annotations.Test
    public void testMatchPassword() {
        System.out.println("matchPassword");
        String rawPassword = "";
        String hashPassword = "";
        SecurityService instance = new SecurityService();
        boolean expResult = false;
        //boolean result = instance.matchPassword(rawPassword, hashPassword);
        //assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getEncryptedPassword method, of class SecurityService.
     */
    @org.testng.annotations.Test
    public void testGetEncryptedPassword() {
        System.out.println("getEncryptedPassword");
        String password = "";
        SecurityService instance = new SecurityService();
        String expResult = "";
        //String result = instance.getEncryptedPassword(password);
        //assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
}
