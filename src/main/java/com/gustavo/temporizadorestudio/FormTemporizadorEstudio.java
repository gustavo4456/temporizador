/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.gustavo.temporizadorestudio;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author gustavo
 */
public class FormTemporizadorEstudio extends javax.swing.JFrame {

    /**
     * Creates new form FormTemporizadorEstudio
     */
    private Timer tiempo;
    private Timer tiempoDescanso;
    private int minutos = 25;
    private int segundos = 0;
    private int minutosDescanso = 5;
    private int segundosDescanso = 0;
    private int contadorSeriesCompletadas = 0;
    private int contadorDescansos = 0;
    private boolean descansoActivado = false;
    private boolean botonesHabilitados = true;
    private boolean botonGuardarHabilitado = false;
    //calculo barra de progreso
    private int minutosCompleto;
    private int segundosCompletos;
    private int minutosCompletoDescanso;
    private int segundosCompletosDescanso;

    private void reiniciarAtributos() {
        minutos = 25;
        segundos = 0;
    }

    private void reiniciarAtributosDescanso() {
        minutosDescanso = 5;
        segundosDescanso = 0;
    }

    private String horaMinutosSegundos() {
        String minutosTotales = String.valueOf((minutosCompleto * contadorSeriesCompletadas) / 60D);

        BigDecimal number = new BigDecimal(minutosTotales);
        BigDecimal b1 = new BigDecimal("60");
        long iPart = number.longValue();
        BigDecimal fraccion = number.remainder(BigDecimal.ONE);

//        System.out.println("Integer part = " + iPart);
//        System.out.println("Fractional part = " + fraccion);
//        
//        System.out.println("0" + iPart + ":" + fraccion.multiply(b1).intValue() + ":" + segundos);
        String texto = (iPart <= 9 ? "0" : "") + iPart + ":" + (fraccion.multiply(b1).intValue() <= 9 ? "0" : "") + fraccion.multiply(b1).intValue() + ":" + (segundos <= 9 ? "0" : "") + segundos;

        System.out.println(texto);

        return texto;
    }

    private ArrayList<ContenidoFilaTabla> leerArchivo() {
        String columnaFecha = "";
        int contadorColumna = 0;
        int contador = 0;
        int posicionAnterior = 0;
        ContenidoFilaTabla contenidoFilaTabla = new ContenidoFilaTabla();
        ArrayList<ContenidoFilaTabla> listaContenidoFilaTabla = new ArrayList<>();

        try {
            Scanner input;
            if (!new File("./resumen.txt").exists()) {
                crearArchivoPorDefecto();
                input = new Scanner(new File("./resumen.txt"));
            } else {
                input = new Scanner(new File("./resumen.txt"));
            }

            while (input.hasNextLine()) {
                String linea = input.nextLine();
                linea.trim();

                if (contador > 0) {
                    for (int i = 0; i < linea.length(); i++) {
                        if (linea.charAt(i) == ',' && posicionAnterior < linea.length()) {

                            columnaFecha = linea.substring(posicionAnterior, i);

                            switch (contadorColumna) {
                                case 0:
                                    contenidoFilaTabla.setFecha(columnaFecha);
                                    break;
                                case 1:
                                    contenidoFilaTabla.setTiempoTotal(columnaFecha);
                                    break;
                                case 2:
                                    contenidoFilaTabla.setSeries(columnaFecha);
                                    break;
                                case 3:
                                    contenidoFilaTabla.setDescanso(columnaFecha);
                                    break;
                                default:
                            }

//                            System.out.println(columnaFecha);
                            posicionAnterior = i + 1;

                            contadorColumna++;
                        }
                    }
                    listaContenidoFilaTabla.add(contenidoFilaTabla);
                    contadorColumna = 0;
                    contenidoFilaTabla = new ContenidoFilaTabla();
                }

//                System.out.println(linea);
                posicionAnterior = 0;
                contador++;
            }

            input.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listaContenidoFilaTabla;
    }

    private void crearArchivoPorDefecto() {
        try {
            String contenidoPorDefecto = "Fecha,Tiempo total,Series,Descansos,";
            String ruta = "./resumen.txt";

            File file = new File(ruta);
            // Si el archivo no existe es creado
            if (!file.exists()) {
                file.createNewFile();
                FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(contenidoPorDefecto);
                bw.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void actualizarAchivo(ArrayList<ContenidoFilaTabla> listaContenidoFilaTabla, String fechaFilaAgregar, String tiempoTotalFilaAgregar, String seriesFilaAgregar, String descansoFilaAgregar) {
        try {
            int contador = 0;
            boolean banderaHablitadaFilaNueva = false;

            String contenidoPorDefecto = "Fecha,Tiempo total,Series,Descansos,";
            String contenido = "";
            String ruta = "./resumen.txt";

            contenido = contenido + contenidoPorDefecto;

            for (ContenidoFilaTabla contenidoFilaTabla : listaContenidoFilaTabla) {
                contador++;
                if (contador == (listaContenidoFilaTabla.size())) {
                    if (contenidoFilaTabla.getFecha().equals(LocalDate.now().toString())) {
                        contenido = contenido + "\n" + fechaFilaAgregar + "," + tiempoTotalFilaAgregar + "," + seriesFilaAgregar + "," + descansoFilaAgregar + ",";
                        banderaHablitadaFilaNueva = false;
                    } else {
                        contenido = contenido + "\n" + contenidoFilaTabla.getFecha() + "," + contenidoFilaTabla.getTiempoTotal() + "," + contenidoFilaTabla.getSeries() + "," + contenidoFilaTabla.getDescanso() + ",";
                        banderaHablitadaFilaNueva = true;
                    }
                    System.out.println(contador + " == " + listaContenidoFilaTabla.size());
                } else {
                    contenido = contenido + "\n" + contenidoFilaTabla.getFecha() + "," + contenidoFilaTabla.getTiempoTotal() + "," + contenidoFilaTabla.getSeries() + "," + contenidoFilaTabla.getDescanso() + ",";
                }

            }

            if (banderaHablitadaFilaNueva || listaContenidoFilaTabla.isEmpty()) {
                contenido = contenido + "\n" + fechaFilaAgregar + "," + tiempoTotalFilaAgregar + "," + seriesFilaAgregar + "," + descansoFilaAgregar + ",";
            }

            File file = new File(ruta);
            // Si el archivo no existe es creado
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(contenido);

            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void llenarTabla(ArrayList<ContenidoFilaTabla> listaFila) {

        int contador = 0;

        DefaultTableModel tabla = new DefaultTableModel();

        tabla.addColumn("Fecha");
        tabla.addColumn("Tiempo total");
        tabla.addColumn("Series");
        tabla.addColumn("Descanso");

        Object obj[] = null;
        for (int i = 0; i < listaFila.size(); i++) {
            tabla.addRow(obj);
            ContenidoFilaTabla getContenidoFilaTabla = listaFila.get(i);

            tabla.setValueAt(getContenidoFilaTabla.getFecha(), i, 0);
            tabla.setValueAt(getContenidoFilaTabla.getTiempoTotal(), i, 1);
            tabla.setValueAt(getContenidoFilaTabla.getSeries(), i, 2);
            tabla.setValueAt(getContenidoFilaTabla.getDescanso(), i, 3);

        }

//        tabla.addRow(fila);
        tablaResumen.setEnabled(false);

        tablaResumen.setModel(tabla);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tabla);
        tablaResumen.setRowSorter(sorter);

        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.DESCENDING));
        sorter.setSortKeys(sortKeys);

    }

    private ActionListener accionesDescanso = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            descansoActivado = true;
            txtMensaje.setText("DESCANSAR");
            txtInicioPausa.setText("Pausa");
            if (segundosDescanso == -1) {
                minutosDescanso--;
                segundosDescanso = 59;
            }

            actualizarLabelTemporizadorDescanso();
            if (minutosDescanso == 0 && segundosDescanso == 0) {
                botonGuardarHabilitado = true;
                txtGuardar.setEnabled(true);

                txtInicioPausa.setText("Inciar");
                contadorDescansos++;
                minutosDescanso = 5;
                txtTemporizador.setForeground(Color.black);

//                minutos = 0;
//                segundos = 5;
                reiniciarAtributos();
                actualizarLabelTemporizador();
                descansoActivado = false;

                tiempoDescanso.stop();
                txtMensaje.setText("ESTUDIAR");

            }
            cargarBarraProgresoDescanso();
            segundosDescanso--;

        }
    };

    private ActionListener acciones = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            if (segundos == -1) {
                minutos--;
                segundos = 59;
            }

            actualizarLabelTemporizador();
            if (minutos == 0 && segundos == 0) {
                txtInicioPausa.setText("Inciar");
                contadorSeriesCompletadas++;
                minutos = 25;
                switch (contadorSeriesCompletadas) {
                    case 1:
                        txtSerie1.setBackground(Color.green);
                        txtTemporizador.setForeground(Color.blue);
                        tiempoDescanso.start();
                        break;

                    case 2:
                        txtSerie2.setBackground(Color.green);
                        txtTemporizador.setForeground(Color.blue);
                        tiempoDescanso.start();
                        break;
                    case 3:
                        txtSerie3.setBackground(Color.green);
                        txtTemporizador.setForeground(Color.blue);
                        tiempoDescanso.start();
                        break;

                    case 4:
                        txtSerie4.setBackground(Color.green);
                        txtTemporizador.setForeground(Color.blue);
                        tiempoDescanso.start();
                        break;
                    case 5:
                        txtSerie5.setBackground(Color.green);
                        txtTemporizador.setForeground(Color.blue);
                        tiempoDescanso.start();
                        break;

                    case 6:
                        txtSerie6.setBackground(Color.green);
                        txtTemporizador.setForeground(Color.blue);
                        tiempoDescanso.start();
                        break;
                    case 7:
                        txtSerie7.setBackground(Color.green);
                        txtTemporizador.setForeground(Color.blue);
                        tiempoDescanso.start();
                        break;

                    case 8:
                        minutos = 0;
                        segundos = 0;
                        txtSerie8.setBackground(Color.green);
//                        txtTemporizador.setForeground(Color.blue);
                        botonesHabilitados = false;
                        txtReiniciar.setEnabled(false);
                        txtInicioPausa.setEnabled(false);
                        txtGuardar.setEnabled(false);

                        ArrayList<ContenidoFilaTabla> listaContenidoFilaTablas = leerArchivo();
                        actualizarAchivo(listaContenidoFilaTablas, LocalDate.now().toString(), horaMinutosSegundos(), String.valueOf(contadorSeriesCompletadas), String.valueOf(contadorDescansos));
                        listaContenidoFilaTablas = leerArchivo();
                        llenarTabla(listaContenidoFilaTablas);
                        break;

                    default:

                }

//                txtGuardar.setEnabled(false);
                segundos = 1;
                tiempo.stop();

            }
            botonGuardarHabilitado = false;
            cargarBarraProgreso();
            segundos--;

        }

    };

    private void cargarBarraProgreso() {
        int minutosAsegundos = (minutos * 60) + segundos;
        int minutosAsegundosCompletos = (minutosCompleto * 60) + segundosCompletos;
//        this.setTitle(minutosAsegundos + " " + minutosAsegundosCompletos);

        if (minutosAsegundos > 0 && minutosAsegundosCompletos > 0) {
//            this.setTitle(minutosAsegundos + " " + minutosCompleto);
            int valorBarra = (minutosAsegundos * 100) / minutosAsegundosCompletos;
            barraProgresion.setValue(valorBarra);
        } else {
            barraProgresion.setValue(0);
        }

    }

    private void cargarBarraProgresoDescanso() {
        int minutosAsegundos = (minutosDescanso * 60) + segundosDescanso;
        int minutosAsegundosCompletos = (minutosCompletoDescanso * 60) + segundosCompletosDescanso;
//        this.setTitle(minutosAsegundos + " " + minutosAsegundosCompletos);

        if (minutosAsegundos > 0 && minutosAsegundosCompletos > 0) {
//            this.setTitle(minutosAsegundos + " " + minutosCompleto);
            int valorBarra = (minutosAsegundos * 100) / minutosAsegundosCompletos;
            barraProgresion.setValue(valorBarra);
        } else {
            barraProgresion.setValue(0);
        }

    }

    private void actualizarLabelTemporizador() {
        String texto = (minutos <= 9 ? "0" : "") + minutos + ":" + (segundos <= 9 ? "0" : "") + segundos;
        txtTemporizador.setText(texto);
    }

    private void actualizarLabelTemporizadorDescanso() {
        String texto = (minutosDescanso <= 9 ? "0" : "") + minutosDescanso + ":" + (segundosDescanso <= 9 ? "0" : "") + segundosDescanso;
        txtTemporizador.setText(texto);
    }

    public FormTemporizadorEstudio() {
        initComponents();
        horaMinutosSegundos();
        crearArchivoPorDefecto();
        ArrayList<ContenidoFilaTabla> listaContenidoFilaTablas = leerArchivo();
        llenarTabla(listaContenidoFilaTablas);

        minutosCompleto = minutos;
        segundosCompletos = segundos;
        minutosCompletoDescanso = minutosDescanso;
        segundosCompletosDescanso = segundosDescanso;

        leerArchivo();
        this.setLocationRelativeTo(null);
        this.setTitle("Temporizador estudio");
        actualizarLabelTemporizador();

        tiempo = new Timer(1000, acciones);
        tiempoDescanso = new Timer(1000, accionesDescanso);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pestanias = new javax.swing.JTabbedPane();
        panelAdministracionTemporizador = new javax.swing.JPanel();
        panelBarraProgresion = new javax.swing.JPanel();
        barraProgresion = new javax.swing.JProgressBar();
        panelTemporizador = new javax.swing.JPanel();
        txtTemporizador = new javax.swing.JLabel();
        panelSeries = new javax.swing.JPanel();
        txtSerie1 = new javax.swing.JLabel();
        txtSerie2 = new javax.swing.JLabel();
        txtSerie3 = new javax.swing.JLabel();
        txtSerie4 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtSerie5 = new javax.swing.JLabel();
        txtSerie6 = new javax.swing.JLabel();
        txtSerie7 = new javax.swing.JLabel();
        txtSerie8 = new javax.swing.JLabel();
        panelTiempoTotal = new javax.swing.JPanel();
        txtMensaje = new javax.swing.JLabel();
        panelBotones = new javax.swing.JPanel();
        txtReiniciar = new javax.swing.JLabel();
        txtInicioPausa = new javax.swing.JLabel();
        txtGuardar = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaResumen = new javax.swing.JTable();
        panelTarea = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panelBarraProgresion.setLayout(new java.awt.BorderLayout());
        panelBarraProgresion.add(barraProgresion, java.awt.BorderLayout.CENTER);

        panelTemporizador.setLayout(new java.awt.BorderLayout());

        txtTemporizador.setFont(new java.awt.Font("sansserif", 0, 48)); // NOI18N
        txtTemporizador.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtTemporizador.setText("00:00");
        txtTemporizador.setOpaque(true);
        panelTemporizador.add(txtTemporizador, java.awt.BorderLayout.CENTER);

        txtSerie1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtSerie1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtSerie1.setOpaque(true);
        txtSerie1.setPreferredSize(new java.awt.Dimension(25, 25));
        panelSeries.add(txtSerie1);

        txtSerie2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtSerie2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtSerie2.setOpaque(true);
        txtSerie2.setPreferredSize(new java.awt.Dimension(25, 25));
        panelSeries.add(txtSerie2);

        txtSerie3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtSerie3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtSerie3.setOpaque(true);
        txtSerie3.setPreferredSize(new java.awt.Dimension(25, 25));
        panelSeries.add(txtSerie3);

        txtSerie4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtSerie4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtSerie4.setOpaque(true);
        txtSerie4.setPreferredSize(new java.awt.Dimension(25, 25));
        panelSeries.add(txtSerie4);

        jLabel10.setText("     ");
        panelSeries.add(jLabel10);

        txtSerie5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtSerie5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtSerie5.setOpaque(true);
        txtSerie5.setPreferredSize(new java.awt.Dimension(25, 25));
        panelSeries.add(txtSerie5);

        txtSerie6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtSerie6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtSerie6.setOpaque(true);
        txtSerie6.setPreferredSize(new java.awt.Dimension(25, 25));
        panelSeries.add(txtSerie6);

        txtSerie7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtSerie7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtSerie7.setOpaque(true);
        txtSerie7.setPreferredSize(new java.awt.Dimension(25, 25));
        panelSeries.add(txtSerie7);

        txtSerie8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtSerie8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtSerie8.setOpaque(true);
        txtSerie8.setPreferredSize(new java.awt.Dimension(25, 25));
        panelSeries.add(txtSerie8);

        txtMensaje.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        txtMensaje.setText("ESTUDIAR");
        panelTiempoTotal.add(txtMensaje);

        txtReiniciar.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        txtReiniciar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtReiniciar.setText("Reiniciar");
        txtReiniciar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtReiniciar.setOpaque(true);
        txtReiniciar.setPreferredSize(new java.awt.Dimension(80, 40));
        txtReiniciar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtReiniciarMouseClicked(evt);
            }
        });
        panelBotones.add(txtReiniciar);

        txtInicioPausa.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        txtInicioPausa.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtInicioPausa.setText("Iniciar");
        txtInicioPausa.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtInicioPausa.setOpaque(true);
        txtInicioPausa.setPreferredSize(new java.awt.Dimension(120, 40));
        txtInicioPausa.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtInicioPausaMouseClicked(evt);
            }
        });
        panelBotones.add(txtInicioPausa);

        txtGuardar.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        txtGuardar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtGuardar.setText("Guardar");
        txtGuardar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtGuardar.setEnabled(false);
        txtGuardar.setOpaque(true);
        txtGuardar.setPreferredSize(new java.awt.Dimension(80, 40));
        txtGuardar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtGuardarMouseClicked(evt);
            }
        });
        panelBotones.add(txtGuardar);

        tablaResumen.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tablaResumen);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 547, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelAdministracionTemporizadorLayout = new javax.swing.GroupLayout(panelAdministracionTemporizador);
        panelAdministracionTemporizador.setLayout(panelAdministracionTemporizadorLayout);
        panelAdministracionTemporizadorLayout.setHorizontalGroup(
            panelAdministracionTemporizadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelBarraProgresion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelTemporizador, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelSeries, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelTiempoTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelBotones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelAdministracionTemporizadorLayout.setVerticalGroup(
            panelAdministracionTemporizadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAdministracionTemporizadorLayout.createSequentialGroup()
                .addComponent(panelBarraProgresion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(panelTemporizador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(panelSeries, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelTiempoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelBotones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pestanias.addTab("Temporizador", panelAdministracionTemporizador);

        javax.swing.GroupLayout panelTareaLayout = new javax.swing.GroupLayout(panelTarea);
        panelTarea.setLayout(panelTareaLayout);
        panelTareaLayout.setHorizontalGroup(
            panelTareaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 547, Short.MAX_VALUE)
        );
        panelTareaLayout.setVerticalGroup(
            panelTareaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 701, Short.MAX_VALUE)
        );

        pestanias.addTab("Tareas", panelTarea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pestanias)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pestanias)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtInicioPausaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtInicioPausaMouseClicked
        // TODO add your handling code here:
        if (botonesHabilitados) {
            txtGuardar.setEnabled(false);

            if (descansoActivado) {
                if (tiempoDescanso.isRunning()) {
                    tiempoDescanso.stop();
                    txtInicioPausa.setText("Iniciar");
                } else {
                    tiempoDescanso.start();
                    txtInicioPausa.setText("Pausa");
                }
            } else {
                if (tiempo.isRunning()) {
                    tiempo.stop();
                    txtInicioPausa.setText("Iniciar");
                } else {
                    tiempo.start();
                    txtInicioPausa.setText("Pausa");
                }
            }
        }


    }//GEN-LAST:event_txtInicioPausaMouseClicked

    private void txtReiniciarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtReiniciarMouseClicked
        // TODO add your handling code here:
        if (botonesHabilitados) {
            txtGuardar.setEnabled(true);
            botonGuardarHabilitado = true;

            if (descansoActivado) {
                tiempoDescanso.stop();
                cargarBarraProgresoDescanso();
                txtTemporizador.setForeground(Color.blue);
                reiniciarAtributosDescanso();
                txtInicioPausa.setText("Iniciar");
                actualizarLabelTemporizadorDescanso();
                cargarBarraProgresoDescanso();

            } else {
                tiempo.stop();
                txtTemporizador.setForeground(Color.black);
                reiniciarAtributos();
                txtInicioPausa.setText("Iniciar");
                actualizarLabelTemporizador();
                cargarBarraProgreso();

            }
        }


    }//GEN-LAST:event_txtReiniciarMouseClicked

    private void txtGuardarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtGuardarMouseClicked
        // TODO add your handling code here:
        if (botonesHabilitados && botonGuardarHabilitado) {
            ArrayList<ContenidoFilaTabla> listaContenidoFilaTablas = leerArchivo();
            actualizarAchivo(listaContenidoFilaTablas, LocalDate.now().toString(), horaMinutosSegundos(), String.valueOf(contadorSeriesCompletadas), String.valueOf(contadorDescansos));
            listaContenidoFilaTablas = leerArchivo();
            llenarTabla(listaContenidoFilaTablas);
        }


    }//GEN-LAST:event_txtGuardarMouseClicked

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
            java.util.logging.Logger.getLogger(FormTemporizadorEstudio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormTemporizadorEstudio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormTemporizadorEstudio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormTemporizadorEstudio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormTemporizadorEstudio().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barraProgresion;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panelAdministracionTemporizador;
    private javax.swing.JPanel panelBarraProgresion;
    private javax.swing.JPanel panelBotones;
    private javax.swing.JPanel panelSeries;
    private javax.swing.JPanel panelTarea;
    private javax.swing.JPanel panelTemporizador;
    private javax.swing.JPanel panelTiempoTotal;
    private javax.swing.JTabbedPane pestanias;
    private javax.swing.JTable tablaResumen;
    private javax.swing.JLabel txtGuardar;
    private javax.swing.JLabel txtInicioPausa;
    private javax.swing.JLabel txtMensaje;
    private javax.swing.JLabel txtReiniciar;
    private javax.swing.JLabel txtSerie1;
    private javax.swing.JLabel txtSerie2;
    private javax.swing.JLabel txtSerie3;
    private javax.swing.JLabel txtSerie4;
    private javax.swing.JLabel txtSerie5;
    private javax.swing.JLabel txtSerie6;
    private javax.swing.JLabel txtSerie7;
    private javax.swing.JLabel txtSerie8;
    private javax.swing.JLabel txtTemporizador;
    // End of variables declaration//GEN-END:variables
}
