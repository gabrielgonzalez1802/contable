package com.contable.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.contable.model.Abono;
import com.contable.model.Carpeta;
import com.contable.model.Cliente;
import com.contable.model.DescuentoDetalle;
import com.contable.model.DetalleMultiPrestamo;
import com.contable.model.Empresa;
import com.contable.model.Prestamo;
import com.contable.model.PrestamoAdicional;
import com.contable.model.PrestamoDetalle;
import com.contable.model.PrestamoInteresDetalle;
import com.contable.model.PrestamoReportePendiente;
import com.contable.model.Usuario;
import com.contable.service.IAbonosService;
import com.contable.service.ICarpetasService;
import com.contable.service.IClientesService;
import com.contable.service.IDescuentosDetallesService;
import com.contable.service.IPrestamosAdicionalesService;
import com.contable.service.IPrestamosDetallesService;
import com.contable.service.IPrestamosInteresesDetallesService;
import com.contable.service.IPrestamosService;
import com.contable.service.IUsuariosService;

@Controller
@RequestMapping("/reportes")
public class ReportesController {
	
	@Autowired
	private IPrestamosService servicePrestamos;
	
	@Autowired
	private ICarpetasService serviceCarpetas;
	
	@Autowired
	private IClientesService serviceClientes;
	
	@Autowired
	private IAbonosService serviceAbonos;
	
	@Autowired
	private IUsuariosService serviceUsuarios;
	
	@Autowired
	private IDescuentosDetallesService serviceDescuentosDetalles;
	
	@Autowired
	private IPrestamosInteresesDetallesService servicePrestamosInteresesDetalles;
	
	@Autowired
	private IPrestamosDetallesService servicePrestamosDetalles;
	
	@Autowired
	private IPrestamosAdicionalesService servicePrestamosAdicionales;
	
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    
    List<Prestamo> prestamos = new LinkedList<>();
    
    List<DescuentoDetalle> descuentosDetalles = new LinkedList<>();
    
	List<PrestamoReportePendiente> prestamosReportesPendientes = new LinkedList<>();
    
    List<Abono> abonos = new LinkedList<>();

	@GetMapping("/listaReportes")
	public String listaReportes(Model model, HttpSession session) {
		List<Cliente> clientes = serviceClientes.buscarTodos();
		Carpeta carpeta = new Carpeta();
		if(session.getAttribute("carpeta") != null) {
			carpeta = serviceCarpetas.buscarPorId((int) session.getAttribute("carpeta"));
		}else {
			//Cargamos la principal
			if(session.getAttribute("empresa")!=null) {
				carpeta = serviceCarpetas.buscarTipoCarpetaEmpresa(1, (Empresa) session.getAttribute("empresa")).get(0);
			}
		}
		model.addAttribute("clientes", clientes);
		model.addAttribute("carpeta", carpeta);
		return "impresiones/listaReportes :: listaReportes";
	}
	
	@GetMapping("/listaPrestamos/{desde}/{hasta}")
	public String listaPrestamos(Model model, HttpSession session, @PathVariable("desde") String desde,
			@PathVariable("hasta") String hasta) throws ParseException {
		
		Integer idCarpeta = null;
		Carpeta carpeta = null;
		Date dateDesde = formatter.parse(desde);
		Date dateHasta = formatter.parse(hasta);
		
		if(session.getAttribute("carpeta") != null) {
			idCarpeta = (Integer) session.getAttribute("carpeta");
		}
		
		if(idCarpeta!=null) {
			carpeta = serviceCarpetas.buscarPorId(idCarpeta);
		}else {
			List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpetaEmpresa(1, (Empresa) session.getAttribute("empresa"));
			carpeta = carpetas.get(0);
		}
		
		double totalMonto = 0.0;
		
		prestamos = servicePrestamos.
				buscarPorCarpetaEmpresaFechasPorFechaDesc(carpeta,(Empresa) session.getAttribute("empresa"), dateDesde, dateHasta);
		
		for (Prestamo prestamo : prestamos) {
			totalMonto += prestamo.getMonto().doubleValue();
		}
		
		model.addAttribute("prestamos", prestamos);
		model.addAttribute("totalMonto", formato2d(totalMonto));
		return "impresiones/prestamos/listaPrestamos :: listaPrestamos";
	}
	
	@GetMapping("/porCobrar")
	public String porCobrar(Model model, HttpSession session) {
		
		prestamosReportesPendientes = new LinkedList<PrestamoReportePendiente>();
		
		Integer idCarpeta = null;
		Carpeta carpeta = null;
		
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		
		if(session.getAttribute("carpeta") != null) {
			idCarpeta = (Integer) session.getAttribute("carpeta");
		}
		
		if(idCarpeta!=null) {
			carpeta = serviceCarpetas.buscarPorId(idCarpeta);
		}else {
			List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpetaEmpresa(1, empresa);
			carpeta = carpetas.get(0);
		}
		
		double totalMonto = 0.0;
						
		List<Prestamo> prestamosTemp = servicePrestamos.buscarPorCarpetaEmpresa(carpeta, empresa);
		
		for (Prestamo prestamo : prestamosTemp) {
			
			PrestamoReportePendiente prestamoReportePendiente = new PrestamoReportePendiente();
						
			double moraPendiente = 0.0;
			double adicionalPendiente = 0.0;
			double capitalPendiente = 0.0;
			double interesPendiente = 0.0;
			double prestamoPendiente = 0.0;
			
			prestamoReportePendiente.setPrestamo(prestamo);

			if(prestamo.getTipo().equals("2")) {
				//Interes
				List<PrestamoInteresDetalle> prestamosInteresesDetalles = servicePrestamosInteresesDetalles.buscarPorPrestamo(prestamo);
				for (PrestamoInteresDetalle prestamoInteresDetalle : prestamosInteresesDetalles) {
					moraPendiente += prestamoInteresDetalle.getMora().doubleValue()-prestamoInteresDetalle.getMora_pagada().doubleValue();
					capitalPendiente += prestamoInteresDetalle.getCapital().doubleValue()-prestamoInteresDetalle.getCapital_pagado().doubleValue();
					interesPendiente += prestamoInteresDetalle.getInteres().doubleValue()-prestamoInteresDetalle.getInteres_pagado().doubleValue();
					List<PrestamoAdicional> adicionales = servicePrestamosAdicionales.buscarPorPrestamo(prestamo);
					for (PrestamoAdicional adicional : adicionales) {
						adicionalPendiente += adicional.getMonto().doubleValue()-adicional.getMonto_pagado().doubleValue();
					}
				}
			}else {
				//Cuotas
				List<PrestamoDetalle> prestamosDetalles = servicePrestamosDetalles.buscarPorPrestamo(prestamo);
				for (PrestamoDetalle prestamoDetalle : prestamosDetalles) {
					moraPendiente += prestamoDetalle.getMora().doubleValue()-prestamoDetalle.getMora_pagada().doubleValue();
					capitalPendiente += prestamoDetalle.getCapital().doubleValue()-prestamoDetalle.getCapital_pagado().doubleValue();
					interesPendiente += prestamoDetalle.getInteres().doubleValue()-prestamoDetalle.getInteres_pagado().doubleValue();
					List<PrestamoAdicional> adicionales = servicePrestamosAdicionales.buscarPorPrestamo(prestamo);
					for (PrestamoAdicional adicional : adicionales) {
						adicionalPendiente += adicional.getMonto().doubleValue()-adicional.getMonto_pagado().doubleValue();
					}
				}
				
			}
			prestamoReportePendiente.setAdicionalPendiente(formato2d(adicionalPendiente));
			prestamoReportePendiente.setCapitalPendiente(prestamo.getTipo().equals("2")?formato2d(prestamo.getBalance()):formato2d(capitalPendiente));
			prestamoReportePendiente.setMoraPendiente(formato2d(moraPendiente));
			prestamoReportePendiente.setMontoPrestamo(formato2d(prestamo.getMonto()));
			prestamoReportePendiente.setInteresPendiente(formato2d(interesPendiente));
			prestamoReportePendiente.setPrestamoPendiente(formato2d(prestamoPendiente));
			prestamosReportesPendientes.add(prestamoReportePendiente);
		}
		
		double totalAdicional = 0.0;
		double totalCapital = 0.0;
		double totalInteres = 0.0;
		double totalMora = 0.0;
		
		for (PrestamoReportePendiente pendiente : prestamosReportesPendientes) {
			totalAdicional+=pendiente.getAdicionalPendiente();
			totalMonto+=pendiente.getMontoPrestamo();
			totalCapital+=pendiente.getCapitalPendiente();
			totalInteres+=pendiente.getInteresPendiente();
			totalMora+=pendiente.getMoraPendiente();
		}
		
		model.addAttribute("pendientes", prestamosReportesPendientes);
		model.addAttribute("totalMonto", formato2d(totalMonto));
		model.addAttribute("totalCapital", formato2d(totalCapital));
		model.addAttribute("totalAdicional", formato2d(totalAdicional));
		model.addAttribute("totalInteres", formato2d(totalInteres));
		model.addAttribute("totalMora", formato2d(totalMora));
		return "impresiones/prestamos/listaPendientes :: listaPendientes";
	}
	
//	@GetMapping("/pagosCompra")
//	public String reportePagosCompra(Model model, HttpSession session) {
//		return "impresiones/contabilidad/listaCompraPagos :: ";
//	}
	
	@GetMapping("/listaPendientes")
	public String listaPendientes(Model model, HttpSession session) throws ParseException {
		model.addAttribute("empresa", (Empresa) session.getAttribute("empresa"));
		
		double totalMonto = 0.0;
		double totalAdicional = 0.0;
		double totalCapital = 0.0;
		double totalInteres = 0.0;
		double totalMora = 0.0;
		
		for (PrestamoReportePendiente pendiente : prestamosReportesPendientes) {
			totalAdicional+=pendiente.getAdicionalPendiente();
			totalMonto+=pendiente.getMontoPrestamo();
			totalCapital+=pendiente.getCapitalPendiente();
			totalInteres+=pendiente.getInteresPendiente();
			totalMora+=pendiente.getMoraPendiente();
		}
		model.addAttribute("pendientes", prestamosReportesPendientes);
		model.addAttribute("totalMonto", formato2d(totalMonto));
		model.addAttribute("totalCapital", formato2d(totalCapital));
		model.addAttribute("totalAdicional", formato2d(totalAdicional));
		model.addAttribute("totalInteres", formato2d(totalInteres));
		model.addAttribute("totalMora", formato2d(totalMora));
		return "impresiones/prestamos/listaPendientesImprimir :: #imprimirData";
	}
	
	@GetMapping("/listaDescuentos/{desde}/{hasta}")
	public String listaDescuentos(Model model, HttpSession session, @PathVariable("desde") String desde,
			@PathVariable("hasta") String hasta) throws ParseException {
		
		Integer idCarpeta = null;
		Carpeta carpeta = null;
		Date dateDesde = formatter.parse(desde);
		Date dateHasta = formatter.parse(hasta);
		
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		
		if(session.getAttribute("carpeta") != null) {
			idCarpeta = (Integer) session.getAttribute("carpeta");
		}
		
		if(idCarpeta!=null) {
			carpeta = serviceCarpetas.buscarPorId(idCarpeta);
		}else {
			List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpetaEmpresa(1, empresa);
			carpeta = carpetas.get(0);
		}
		
		double totalMonto = 0.0;
				
		descuentosDetalles = serviceDescuentosDetalles.buscarPorEmpresaCarpetaFechas(empresa, carpeta, dateDesde, dateHasta);
		for (DescuentoDetalle descuentoDetalle : descuentosDetalles) {
			totalMonto+=descuentoDetalle.getTotalDescuento().doubleValue();
		}
		
		model.addAttribute("descuentos", descuentosDetalles);
		model.addAttribute("totalMonto", formato2d(totalMonto));
		return "impresiones/prestamos/listaDescuentos :: listaDescuentos";
	}
	
	@GetMapping("/listaDescuentos")
	public String listaDescuentos(Model model, HttpSession session) throws ParseException {
		model.addAttribute("empresa", (Empresa) session.getAttribute("empresa"));
		
		double totalMonto = 0.0;
		
		for (DescuentoDetalle descuentoDetalle : descuentosDetalles) {
			totalMonto += descuentoDetalle.getTotalDescuento().doubleValue();
		}
		model.addAttribute("totalMonto", formato2d(totalMonto));
		model.addAttribute("descuentos", descuentosDetalles);
		return "impresiones/prestamos/listaDescuentosImprimir :: #imprimirData";
	}

	@GetMapping("/listaPrestamos")
	public String listaPrestamos(Model model, HttpSession session) throws ParseException {
		model.addAttribute("empresa", (Empresa) session.getAttribute("empresa"));
		
		double totalMonto = 0.0;
		
		for (Prestamo prestamo : prestamos) {
			totalMonto += prestamo.getMonto().doubleValue();
		}
		model.addAttribute("totalMonto", formato2d(totalMonto));
		model.addAttribute("prestamos", prestamos);
		return "impresiones/prestamos/listaPrestamosImprimir :: #imprimirData";
	}
	
	@PostMapping("/cuadreCaja")
	public String cuadreCaja(Model model, String fecha, Integer userId,
			HttpSession session) throws ParseException {
		
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		LocalDateTime fechaBusqueda = null;
		
		if(fecha.equals("")) {
			fechaBusqueda = convertToLocalDateTimeViaInstant(new Date());
		}else {
			fechaBusqueda = convertToLocalDateTimeViaInstant(formato.parse(fecha));
		}
		
		Date fechaTemp = convertToDateViaInstant(fechaBusqueda);
		
		Carpeta carpeta = null;
		if(session.getAttribute("carpeta")!=null) {
			carpeta = serviceCarpetas.buscarPorId((Integer) session.getAttribute("carpeta"));
		}else {
			List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpetaEmpresa(1, (Empresa) session.getAttribute("empresa"));
			carpeta = carpetas.get(0);
		}
		
		List<Abono> abonos = new LinkedList<>();
		double sumaEfectivo = 0;
		double sumaCheque = 0;
		double sumaDepositoTransferencia = 0;
		
		List<Abono> abonosTemp = new LinkedList<>();
		
		List<Prestamo> prestamos = servicePrestamos.buscarPorCarpetaEmpresa(carpeta, (Empresa) session.getAttribute("empresa"));
		if(userId>0) {
			Usuario usuarioTemp = serviceUsuarios.buscarPorId(userId);
			abonosTemp = serviceAbonos.buscarPorPrestamosUsuario(prestamos, usuarioTemp);
		}else {
			abonosTemp = serviceAbonos.buscarPorPrestamos(prestamos);
		}
		List<Abono> abonosDepured = new LinkedList<>();
		for (Abono abono : abonosTemp) {
			LocalDateTime fechaAbono = convertToLocalDateTimeViaInstant(abono.getFecha());
			if(fechaBusqueda.getYear() == fechaAbono.getYear() && 
					fechaBusqueda.getMonthValue() == fechaAbono.getMonthValue() && 
							fechaBusqueda.getDayOfMonth() == fechaAbono.getDayOfMonth()) {
				
				abonosDepured.add(abono);
			}
		}
		for (Abono abonoDepured : abonosDepured) {
			sumaEfectivo += abonoDepured.getEfectivo();
			sumaCheque += abonoDepured.getCheque();
			sumaDepositoTransferencia += abonoDepured.getTransferencia_deposito();
			abonos.add(abonoDepured);
		}
		
		List<Usuario> usuarios = serviceUsuarios.buscarPorEstado(1);
		model.addAttribute("usuarios", usuarios);
		model.addAttribute("carpeta", carpeta);
		model.addAttribute("sumaEfectivo", formato2d(sumaEfectivo));
		model.addAttribute("sumaCheque", formato2d(sumaCheque));
		model.addAttribute("sumaDepositoTransferencia", formato2d(sumaDepositoTransferencia));
		model.addAttribute("abonos", abonos);
		model.addAttribute("fecha", fechaTemp);
		model.addAttribute("userAcct", userId);
		model.addAttribute("empresa", (Empresa) session.getAttribute("empresa"));
		return "impresiones/caja/cuadreCaja :: #imprimirData";
	}
	
	@PostMapping("/listaAbonos")
	public String listaAbonos(Model model, HttpSession session, String desde,
			String hasta, Integer idCliente) throws ParseException {
		
		Carpeta carpeta = null;
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Cliente cliente = serviceClientes.buscarPorId(idCliente);
		
		if(session.getAttribute("carpeta") != null) {
			 carpeta = serviceCarpetas.buscarPorId((Integer) session.getAttribute("carpeta"));
		}else {
			List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpetaEmpresa(1, empresa);
			if(!carpetas.isEmpty()) {
				carpeta = carpetas.get(0);
			}
		}
				
		Date dateDesde = null;
		Date dateHasta = null;
		
		if(!desde.equals("") && !hasta.equals("")) {
			dateDesde = formatter.parse(desde);
			dateHasta = formatter.parse(hasta);
		}
		
		if((dateDesde == null || dateHasta == null) && idCliente == 0 ) {
			abonos = serviceAbonos.buscarPorEmpresaCarpeta(empresa,carpeta);
		}else if((dateDesde == null || dateHasta == null) && idCliente > 0) {
			abonos = serviceAbonos.buscarPorClienteEmpresaCarpeta(cliente, empresa, carpeta);
		}else if((dateDesde != null && dateHasta != null) && idCliente == 0) {
			abonos = serviceAbonos.buscarPorEmpresaCarpetaFechas(empresa, carpeta, dateDesde, dateHasta);
		}else if((dateDesde != null && dateHasta != null) && idCliente > 0) {
			abonos = serviceAbonos.buscarPorClienteEmpresaCarpetaFechas(cliente, empresa, carpeta, dateDesde, dateHasta);
		}else {
			abonos = new LinkedList<>();
		}
		
		model.addAttribute("empresa", empresa);
		model.addAttribute("abonos", abonos);
		return "impresiones/caja/listaAbonos :: #imprimirData";
	}
	
	public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
	    return dateToConvert.toInstant()
	      .atZone(ZoneId.systemDefault())
	      .toLocalDateTime();
	}
	
	public Date convertToDateViaInstant(LocalDateTime dateToConvert) {
	    return java.util.Date
	      .from(dateToConvert.atZone(ZoneId.systemDefault())
	      .toInstant());
	}
	
	public double formato2d(double number) {
		number = Math.round(number * 100);
		number = number/100;
		return number;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder webDataBinder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}
}
