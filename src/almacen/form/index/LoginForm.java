
package almacen.form.index;

import common.constants.ApplicationConstants;
import common.constants.SubstanceThemeConstant;
import common.exceptions.DataOriginException;
import common.model.Usuario;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import common.services.UserService;
import java.time.Duration;
import java.time.Instant;
import org.jvnet.substance.SubstanceLookAndFeel;

public class LoginForm extends javax.swing.JFrame {
    
    private static final UserService userService = UserService.getInstance();
    private static final Logger LOGGER = Logger.getLogger(LoginForm.class.getName());
    private static int attemps;
    private static final int ATTEMPT_LIMIT = 3;
    private static final int MINUTES_TO_TRY_AGAIN = 1;
    private static Instant startTime;

    
    public LoginForm() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.txtPassword.requestFocus();
        this.setTitle("INICIO DE SESIÓN");
        SubstanceLookAndFeel.setSkin(SubstanceThemeConstant.BUSINESS_SKIN);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblInfo = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        btnLogin = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblInfo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lblInfo.setText("Introduce tu contraseña:");

        txtPassword.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPasswordKeyPressed(evt);
            }
        });

        btnLogin.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnLogin.setText("Ingresar");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPassword)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 220, Short.MAX_VALUE)
                        .addComponent(btnLogin))
                    .addComponent(lblInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblInfo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnLogin)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

        
    private void checkLogin () {
        String pass = String.valueOf(txtPassword.getPassword());
        if (pass.equals("") || pass.length() >= 50) {
            JOptionPane.showMessageDialog(this, "Contraseña invalida","ERROR",JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (startTime != null && Duration.between(startTime, Instant.now()).toMinutes() < MINUTES_TO_TRY_AGAIN) {
            int seconds = (MINUTES_TO_TRY_AGAIN * 60) - Duration.between(startTime, Instant.now()).toSecondsPart();
            JOptionPane.showMessageDialog(this, "Tiempo para intentar nuevamente: "+ seconds +" segundos","ERROR",JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (attemps > ATTEMPT_LIMIT) {
            JOptionPane.showMessageDialog(this, "Excedido el límite de intentos","ERROR",JOptionPane.ERROR_MESSAGE);
            startTime = Instant.now();
            attemps = 0;
            return;
        }
        try {
            Usuario user = userService.getByPassword(pass.trim());
            if(user == null){
                JOptionPane.showMessageDialog(this, ApplicationConstants.DS_MESSAGE_FAIL_LOGIN, ApplicationConstants.TITLE_MESSAGE_FAIL_LOGIN, JOptionPane.ERROR_MESSAGE);
                this.txtPassword.requestFocus();
                txtPassword.setEnabled(true);
                return;
            }
            new IndexForm(user).setVisible(true);
            this.dispose();
        } catch (DataOriginException e) {
            JOptionPane.showMessageDialog(this, e, "Error", JOptionPane.ERROR_MESSAGE);
            LOGGER.error(e);
        } finally {
            attemps++;
        }
    }
    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        // TODO add your handling code here:
        checkLogin();
    }//GEN-LAST:event_btnLoginActionPerformed

    private void txtPasswordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPasswordKeyPressed
        if (evt.getKeyCode() == 10) {
            checkLogin();
        }
    }//GEN-LAST:event_txtPasswordKeyPressed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LoginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JPasswordField txtPassword;
    // End of variables declaration//GEN-END:variables
}
