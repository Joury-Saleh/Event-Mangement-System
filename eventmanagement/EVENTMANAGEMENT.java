/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package eventmanagement;

import javax.swing.UIManager;

/**
 *
 * @author asus
 */

public class EVENTMANAGEMENT {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         //new shape 
         try {
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }
    } catch (Exception e) {
        
    }

        
        LoginPage p = new LoginPage();
    }
}
    
    
  