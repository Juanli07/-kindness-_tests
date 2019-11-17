/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package programasimulacion;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import chi.chiCuadrada;
import komogorov.Komogorov;


/**
 *
 * @author juan0
 */
public class window extends javax.swing.JFrame {

    /**
     * Creates new form window
     */
    public window() {
        initComponents();
    }
    public ArrayList openRead(String rute){
        String line; 
        BufferedReader arch;
        ArrayList<Double> num = new ArrayList();
        try{
           
            arch = new BufferedReader(new FileReader(rute));
            line = arch.readLine();
            while(line != null){
                TextArea.append(line+"\n");
                num.add(Double.parseDouble(line));
                line = arch.readLine();
            }   
           
        }catch(FileNotFoundException e){
            JOptionPane.showInternalMessageDialog(null, "Error al abrir el archivo");
        } catch (IOException ex) {
            JOptionPane.showInternalMessageDialog(null, "Error al leer el archivo");
        }
        return num;
    }
    public ArrayList divClass(ArrayList<Double> nums){
        String numclass = this.JoptionRun("Indique el número de clases :");
        double menor = nums.get(0);
        double mayor = nums.get(0);
        for(int i = 1; i < nums.size(); i++){
            if(nums.get(i) < menor){
                menor = nums.get(i);
            }else if(nums.get(i) > mayor){
                mayor = nums.get(i);
            }
        }
        ArrayList<Integer> freq = new ArrayList();
        double rangosize = (mayor-menor)/Integer.parseInt(numclass);
        double rangomayor = menor+rangosize;
        double rangomenor = menor;
        int[] rep = new int[Integer.parseInt(numclass)];
        for(int i = 0; i < nums.size(); i++){
            int x = 0;
            rangomayor = menor+rangosize;
            rangomenor = menor;
            while(rangomayor <= mayor){
               if(nums.get(i) >= rangomenor && nums.get(i) <= rangomayor){
                   rep[x]++;
                   break;
               }
               x++;
               rangomenor = rangomayor;
               rangomayor += rangosize;
               
            }
            
        }
        for(int i = 0; i < rep.length; i++){
            freq.add(rep[i]);
        }
        return freq;
    }
    public double average(ArrayList<Double> nums){
        double avg = 0;
        for(int i = 0; i < nums.size(); i++){
            avg += nums.get(i);
        }
        return avg/nums.size();
    }
    public double factorial(double numero){
        double factorial = 1;
        while(numero != 0){
            factorial = factorial * numero;
            numero--;
        }
        return factorial;
    }
    public int sum(ArrayList<Integer> freq){
        int sum = 0;
        for(int i = 0; i < freq.size(); i++){
            sum+= freq.get(i);
        }
        return sum;
    }
    public double Variance(ArrayList<Double> nums, double prom){
        double s = 0;
        for(int i = 0; i < nums.size(); i++){
            s += Math.pow((double)nums.get(i)-prom, 2);
        }
        return s/(nums.size()-1);
    }
    public String JoptionRun(String mensaje){
        String respuesta = null;
        boolean band = true;
        while(band)
        try{
            respuesta = JOptionPane.showInputDialog(mensaje);
            Double.parseDouble(respuesta);
            band = false;
        }catch(NumberFormatException e){
            JOptionPane.showMessageDialog(null,"Por favor indique la cantidad en numeros");
            band = true;
        }
        return respuesta;
    }
    public double[][] chiCuadrada(ArrayList<Integer> freq, ArrayList<Double> nums, javax.swing.JTextArea Result){
        String trust = this.JoptionRun("Indique el nivel de confianza :");
        double lamda = (this.average(nums)+this.Variance(nums, this.average(nums)))/2;
        double[][] table = new double[freq.size()+1][5]; 
        int ant = 0;
        int cont = 0;
        DecimalFormat df = new DecimalFormat("#.0000");
        for(int i = 0; i < freq.size(); i++){
            table[i][0] = i;
            table[i][1] = (double)freq.get(i);
            double dato = (Math.pow(2.71828 , ((-1)*lamda))*(Math.pow(lamda, i)))/this.factorial((double)i);
            table[i][2] = Double.parseDouble(df.format(dato));
            if(Math.round((table[i][2]*(double)this.sum(freq))) < freq.size()){
                table[ant][3] += Math.round((table[i][2]*(double)this.sum(freq)));
                table[freq.size()][3] += Math.round((table[i][2]*(double)this.sum(freq)));
                cont++;
            }else{
                table[i][3] = Math.round((table[i][2]*(double)this.sum(freq)));
                ant = i;
            }
            if(table[i][3] != 0){
               table[i][4] = (Math.pow((table[i][1]-table[i][3]), 2)/table[i][3]);
            }
            table[freq.size()][4] += table[i][4];
            table[freq.size()][1] += table[i][1];
            table[freq.size()][3] += table[i][3];
        }
        
        chiCuadrada preuba = new chiCuadrada();
        double ztab = preuba.returnData((freq.size()-cont), Double.parseDouble(trust));
        if(table[5][4] < ztab){
           Result.append("\n La distribuición es Poisson debido a que Zobs < Ztab\n "+table[5][4]+" < "+ztab);
        }else{
            Result.append("\n La distribuición no es Poisson debido a que Zobs < Ztab no se cumple\n "+table[5][4]+" > "+ztab);
        }
        
        return table;
    }
    public double[][] komogorov(ArrayList<Integer> freq, ArrayList<Double> nums, javax.swing.JTextArea Result){
        String trust = this.JoptionRun("Indique el nivel de confianza :");
        double lamda = ((this.average(nums) + this.Variance(nums, this.average(nums)))/2);
        double[][] table = new double[freq.size()+1][7]; 
        int ant = 0;
        double acum = 0;
        double acum1 = 0;
        DecimalFormat df = new DecimalFormat("#.0000");
        for(int i = 0; i < freq.size(); i++){
            table[i][0] = i;
            table[i][1] = (double)freq.get(i);
            table[i][2] = table[i][1]/this.sum(freq);
            double dato = (Math.pow(2.71828 , ((-1)*lamda))*(Math.pow(lamda, i)))/this.factorial((double)i);
            table[i][3] = Double.parseDouble(df.format(dato));
            table[i][4] += acum += table[i][2];
            table[i][5] += acum1 += table[i][3];
            table[i][6] =  Math.abs(table[i][4]-table[i][5]);
            
            table[freq.size()][3] += table[i][3];
            table[freq.size()][1] += table[i][1];
            table[freq.size()][2] += table[i][2];
        }
        double valor = table[0][6]; 
        for(int i = 1; i < table.length; i++){
            if(table[i][6] > valor){
                valor = table[i][6];
            }
        }
        Komogorov kgv = new Komogorov();
        DecimalFormat tf = new DecimalFormat("#.00");
        double dato = (1-(Double.parseDouble(trust)));
        dato = Double.parseDouble(df.format(dato));
        if(valor < kgv.returnData(this.sum(freq), (dato))){
            Result.append("Se acepta que la hipotesis es nula debido a que la desviación crítica observada es menor que la \ndesviacion critica tabulada\n "+valor+" < "+kgv.returnData(this.sum(freq), (dato)));
        }else{
            Result.append("Se acepta la hipotesis no es nula debido a que la desviación crítica observada no es menor que la \ndesviacion critica tabulada\n "+valor+" > "+kgv.returnData(this.sum(freq), (dato)));
        }
        return table;
    }
    public void setTable(double[][] table, String[] titles, int n, javax.swing.JTable Table){
        String[][] tab = new String[table.length][n];
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < n; j++) {
                tab[i][j] = Double.toString(table[i][j]);
            }
        }
        DefaultTableModel dtm = new DefaultTableModel(tab, titles);
        Table.setModel(dtm);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        Table = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        TextField = new javax.swing.JTextField();
        method = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TextArea = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        Result = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setText("Muestra:");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, -1, -1));

        jButton2.setForeground(new java.awt.Color(255, 51, 51));
        jButton2.setText("RESET");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 450, 72, 69));

        Table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane3.setViewportView(Table);

        getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(108, 80, 580, 235));

        jButton1.setText("Seleccionar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 30, -1, -1));

        TextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextFieldActionPerformed(evt);
            }
        });
        getContentPane().add(TextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 372, -1));

        method.setForeground(new java.awt.Color(255, 0, 51));
        method.setText("*************************");
        getContentPane().add(method, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 320, 152, 20));

        TextArea.setColumns(20);
        TextArea.setRows(5);
        jScrollPane1.setViewportView(TextArea);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 72, 361));

        Result.setColumns(20);
        Result.setRows(5);
        jScrollPane2.setViewportView(Result);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(108, 350, 580, 166));

        jLabel1.setText("Resultado:");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 60, -1, -1));

        jLabel2.setText("Seleccione el archivo .txt a analizar");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 60, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 410, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 80, 60, 410));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 520, -1, 30));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        TextArea.setText("");
        Result.setText("");
        TextField.setText("");
        DefaultTableModel md = new DefaultTableModel();
        Table.setModel(md);
        method.setText("*************************");
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JFileChooser fc=new JFileChooser();
        Component contentPane = null;
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("*.TXT", "txt");
        fc.setFileFilter(filtro);
        int seleccion=fc.showOpenDialog(contentPane);

        if(seleccion==JFileChooser.APPROVE_OPTION){
            File fichero=fc.getSelectedFile();
            TextField.setText(fichero.getAbsolutePath());
            ArrayList<Double> result = this.openRead(fichero.getAbsolutePath());
            ArrayList<Integer> freq = this.divClass(result);
            if(result.size() > 100){
                method.setText("Ji cuadrada  ( χ2 )");
                double[][] table = this.chiCuadrada(freq, result, Result);
                String[] titles = {"N", "Frecuencia observada", "Probabilida(n)", "Frecuencia esperada", "X^2"};
                this.setTable(table, titles, 5, Table);
            }else{
                method.setText("Kolmogorov-Smirnov");
                double[][] table = this.komogorov(freq, result, Result);
                String[] titles = {"N", "Frecuencia observada", "Probabilida(n) obs", "Probabilida(n) teorica", "Frec acumulada obs", "Frec acumulada teorica", "Desviación absoluta"};
                this.setTable(table, titles, 7, Table);
            }
            

        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void TextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TextFieldActionPerformed

    /**
     * @param args the command line arguments
     */
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
            java.util.logging.Logger.getLogger(window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new window().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea Result;
    private javax.swing.JTable Table;
    private javax.swing.JTextArea TextArea;
    private javax.swing.JTextField TextField;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel method;
    // End of variables declaration//GEN-END:variables
}
