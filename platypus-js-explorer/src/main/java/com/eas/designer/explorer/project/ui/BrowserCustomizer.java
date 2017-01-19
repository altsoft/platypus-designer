package com.eas.designer.explorer.project.ui;

import com.eas.designer.application.project.PlatypusProject;
import com.eas.designer.application.project.PlatypusProjectSettings;
import java.awt.EventQueue;
import org.openide.filesystems.FileObject;

/**
 *
 * @author mg
 */
public class BrowserCustomizer extends javax.swing.JPanel {

    protected final PlatypusProject project;
    protected final FileObject appRoot;
    protected final PlatypusProjectSettings projectSettings;

    /**
     * Creates new form ProjectRunningCustomizer
     *
     * @param aProject
     */
    public BrowserCustomizer(PlatypusProject aProject) {
        project = aProject;
        appRoot = aProject.getSrcRoot();
        projectSettings = aProject.getSettings();
        initComponents();
        EventQueue.invokeLater(() -> {
            if (projectSettings.getBrowserCustomUrl() != null) {
                txtBrowserUrl.setText(projectSettings.getBrowserCustomUrl());
            }
            if (projectSettings.getBrowserRunCommand() != null) {
                txtBrowserRunCommand.setText(projectSettings.getBrowserRunCommand());
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblBroswerRunCommand = new javax.swing.JLabel();
        txtBrowserRunCommand = new javax.swing.JTextField();
        lblBrowserUrl = new javax.swing.JLabel();
        txtBrowserUrl = new javax.swing.JTextField();

        lblBroswerRunCommand.setText(org.openide.util.NbBundle.getMessage(BrowserCustomizer.class, "BrowserCustomizer.lblBroswerRunCommand.text")); // NOI18N

        txtBrowserRunCommand.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBrowserRunCommandFocusLost(evt);
            }
        });
        txtBrowserRunCommand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBrowserRunCommandActionPerformed(evt);
            }
        });

        lblBrowserUrl.setText(org.openide.util.NbBundle.getMessage(BrowserCustomizer.class, "BrowserCustomizer.lblBrowserUrl.text")); // NOI18N

        txtBrowserUrl.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBrowserUrlFocusLost(evt);
            }
        });
        txtBrowserUrl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBrowserUrlActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtBrowserRunCommand)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblBroswerRunCommand, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblBrowserUrl, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 230, Short.MAX_VALUE))
                    .addComponent(txtBrowserUrl))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblBrowserUrl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBrowserUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 116, Short.MAX_VALUE)
                .addComponent(lblBroswerRunCommand)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBrowserRunCommand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtBrowserUrlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBrowserUrlActionPerformed
        projectSettings.setBrowserCustomUrl(txtBrowserUrl.getText());
    }//GEN-LAST:event_txtBrowserUrlActionPerformed

    private void txtBrowserUrlFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBrowserUrlFocusLost
        projectSettings.setBrowserCustomUrl(txtBrowserUrl.getText());
    }//GEN-LAST:event_txtBrowserUrlFocusLost

    private void txtBrowserRunCommandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBrowserRunCommandActionPerformed
        projectSettings.setBrowserRunCommand(txtBrowserRunCommand.getText());
    }//GEN-LAST:event_txtBrowserRunCommandActionPerformed

    private void txtBrowserRunCommandFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBrowserRunCommandFocusLost
        projectSettings.setBrowserRunCommand(txtBrowserRunCommand.getText());
    }//GEN-LAST:event_txtBrowserRunCommandFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblBroswerRunCommand;
    private javax.swing.JLabel lblBrowserUrl;
    private javax.swing.JTextField txtBrowserRunCommand;
    private javax.swing.JTextField txtBrowserUrl;
    // End of variables declaration//GEN-END:variables
}