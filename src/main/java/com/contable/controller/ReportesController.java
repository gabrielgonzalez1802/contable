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
import com.contable.model.Amortizacion;
import com.contable.model.Carpeta;
import com.contable.model.Cliente;
import com.contable.model.DescuentoDetalle;
import com.contable.model.DetalleMultiPrestamo;
import com.contable.model.Empresa;
import com.contable.model.Prestamo;
import com.contable.model.PrestamoAdicional;
import com.contable.model.PrestamoCalculoTotal;
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
	
	List<PrestamoCalculoTotal> prestamosTotales = new LinkedList<>();
	
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    
    List<Prestamo> prestamos = new LinkedList<>();
    
    List<DescuentoDetalle> descuentosDetalles = new LinkedList<>();
    
	List<PrestamoReportePendiente> prestamosReportesPendientes = new LinkedList<>();
    
    List<Abono> abonos = new LinkedList<>();
    
    double reusableTotalCapital = 0;
    double reusableTotalInteres = 0;
    double reusableTotalMora = 0;
    double reusableTotalAdicional = 0;

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
	
	@PostMapping("/buscarPrestamosXCobrar")
	public String buscarPrestamosXCobrar(Model model, HttpSession session, 
			String clienteId, String moneda
			) throws ParseException {
		Empresa empresa = (Empresa) session.getAttribute("empresa");

		prestamosTotales.clear();
		
		reusableTotalCapital = 0;
		reusableTotalInteres = 0;
		reusableTotalMora = 0;
		reusableTotalAdicional = 0;
		
		Carpeta carpeta = null;

		if(moneda.equals("peso")) {
			moneda = "pesos";
		}
		
		double totalAdicionales = 0;
		double totalCapital = 0;
		double totalInteres = 0;
		double totalMora = 0;
		
		double totalPesoAdicionales = 0;
		double totalPesoCapital = 0;
		double totalPesoInteres = 0;
		double totalPesoMora = 0;
		
		double totalDolarAdicionales = 0;
		double totalDolarCapital = 0;
		double totalDolarInteres = 0;
		double totalDolarMora = 0;
		
		if(session.getAttribute("carpeta") != null) {
			 carpeta = serviceCarpetas.buscarPorId((Integer) session.getAttribute("carpeta"));
		}else {
			List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpetaEmpresa(1, empresa);
			if(!carpetas.isEmpty()) {
				carpeta = carpetas.get(0);
			}
		}
				
		List<Prestamo> prestamos = new LinkedList<>();
		
		List<Integer> estados = new LinkedList<>();
		estados.add(1);
		
		if(!clienteId.equals("")) {
			Cliente cliente = serviceClientes.buscarPorId(Integer.parseInt(clienteId));
			prestamos = servicePrestamos.buscarPorClienteCarpetaEmpresaMonedaEstadoNotIn(cliente, carpeta, empresa, moneda, estados);
		}else {
			prestamos = servicePrestamos.buscarPorCarpetaEmpresaMonedaEstadoNotIn(carpeta, empresa, moneda, estados);
		}

		for (Prestamo prestamo : prestamos) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");  
			List<Amortizacion> detalles = new LinkedList<>();
			List<PrestamoInteresDetalle> prestamoInteresDetalles = servicePrestamosInteresesDetalles.buscarPorPrestamo(prestamo);
			double totalPendiente = 0;
			double sumaInteresGen = 0;
			double sumaMora = 0;
			double sumaCargo = 0;
			double sumaAbono = 0;
			double sumaBalance = 0;
			
			if(prestamo.getTipo().equals("2")) {
				//Interes
				int count = 0;
				double mora = 0;
				double interes = 0;
				double capital = 0;
				if(!prestamoInteresDetalles.isEmpty()) {
					for (PrestamoInteresDetalle prestamoInteresDetalle : prestamoInteresDetalles) {
						count++;
						String estado = "Normal";
						if(prestamoInteresDetalle.getEstado()==2) {
							estado = "Atraso";
						}else if(prestamoInteresDetalle.getEstado()==1) {
							estado = "Saldo";
						}else if(prestamoInteresDetalle.getEstado()==3) {
							estado = "Legal";
						}
						
						double cargoPagadoTemp = 0;
						
						Amortizacion amortizacion = new Amortizacion();
						
						double descuentoAdicionales = 0;
						double descuentoCargo = 0;
						
						List<PrestamoAdicional> adicionales = servicePrestamosAdicionales.buscarPorPrestamoNumeroCuota(prestamoInteresDetalle.getPrestamo(), prestamoInteresDetalle.getNumero_cuota());
						if(!adicionales.isEmpty()) {
							double cargo = 0;
							for (PrestamoAdicional adicionalTemp : adicionales) {
								cargo+=adicionalTemp.getMonto()-adicionalTemp.getMonto_pagado();
								cargoPagadoTemp+=adicionalTemp.getMonto_pagado();
								descuentoAdicionales+=adicionalTemp.getDescuento_adicionales();
							}
							amortizacion.setCargo(cargo);
							descuentoCargo = amortizacion.getCargo()-descuentoAdicionales;
							amortizacion.setCargo(cargo-descuentoAdicionales);
						}
						
						LocalDateTime fechaTemp = convertToLocalDateTimeViaInstant(prestamoInteresDetalle.getFecha_cuota()).minusMonths(1);
						LocalDateTime dateAcct = LocalDateTime.now();
						int vencidos = (int) diasVencidos(fechaTemp, dateAcct);
						double interesXHoy = prestamoInteresDetalle.getInteres()/30.00*vencidos;
						
						if(interesXHoy>=prestamoInteresDetalle.getInteres()) {
							interesXHoy = prestamoInteresDetalle.getInteres()-prestamoInteresDetalle.getInteres_pagado();
						}
						
						double balance = (prestamoInteresDetalle.getInteres()-prestamoInteresDetalle.getInteres_pagado())+(prestamoInteresDetalle.getMora()-prestamoInteresDetalle.getMora_pagada()-prestamoInteresDetalle.getDescuento_mora())+(amortizacion.getCargo()==null?0:descuentoCargo);
						double abono = prestamoInteresDetalle.getInteres_pagado()+prestamoInteresDetalle.getMora_pagada()+cargoPagadoTemp;
						
						amortizacion.setBalance(formato2d(balance));
						amortizacion.setAbono(formato2d(abono));
						amortizacion.setInteresXhoy(balance==0?0:formato2d(interesXHoy));
						amortizacion.setNumero(count);
						amortizacion.setFecha(sdf.format(prestamoInteresDetalle.getFecha_cuota()));
						capital = prestamoInteresDetalle.getCapital();
						capital-= prestamoInteresDetalle.getCapital_pagado();
						amortizacion.setInteres(formato2d(prestamoInteresDetalle.getInteres()-prestamoInteresDetalle.getInteres_pagado()));
						amortizacion.setTipo(2);
						
						mora = prestamoInteresDetalle.getMora()-prestamoInteresDetalle.getMora_pagada()-prestamoInteresDetalle.getDescuento_mora();

						amortizacion.setMora(formato2d(mora));
						amortizacion.setEstado(estado);
						amortizacion.setAtraso(prestamoInteresDetalle.getDias_atraso());
						
						double descuentoAdicional = 0;
						
						List<PrestamoAdicional> adicionalTemp = servicePrestamosAdicionales
								.buscarPorPrestamoNumeroCuota(prestamo, prestamoInteresDetalle.getNumero_cuota());
						for (PrestamoAdicional adicional : adicionalTemp) {
							descuentoAdicional += adicional.getDescuento_adicionales();
						}

						amortizacion.setDescuento(prestamoInteresDetalle.getDescuento_mora()+descuentoAdicional);
						
						amortizacion.setId(prestamoInteresDetalle.getId());
						detalles.add(amortizacion);
						mora+=prestamoInteresDetalle.getMora();
						interes+=prestamoInteresDetalle.getInteres();
						
						sumaInteresGen+= amortizacion.getInteres();
						sumaMora += amortizacion.getMora();
						sumaAbono += amortizacion.getAbono();
						sumaBalance += amortizacion.getBalance();
											
					}
					detalles.get(0).setCuota(formato2d(mora+interes));
					detalles.get(0).setCapital(formato2d(capital));
				}
				
				List<PrestamoAdicional> pretamoAdicionalTemp = servicePrestamosAdicionales.buscarPorPrestamoEstado(prestamo, 0);
				for (PrestamoAdicional prestamoAdicional : pretamoAdicionalTemp) {
					sumaCargo+=prestamoAdicional.getMonto()-prestamoAdicional.getMonto_pagado()-prestamoAdicional.getDescuento_adicionales();
				}
				
			}else {
				//Cuotas
				
				int count = 0;
				double mora = 0;
				double interes = 0;
				double capital = 0;

				List<PrestamoDetalle> prestamoDetalles = servicePrestamosDetalles.buscarPorPrestamo(prestamo);
				
				detalles = new LinkedList<>();
				for (PrestamoDetalle prestamoDetalle : prestamoDetalles) {
					
					List<PrestamoAdicional> adicionales = servicePrestamosAdicionales.buscarPorPrestamoNumeroCuota(prestamoDetalle.getPrestamo(), prestamoDetalle.getNumero());

					Amortizacion amortizacion = new Amortizacion();
					
					double cargo = 0;
					
					count++;
					String estado = "Normal";
					if(prestamoDetalle.getEstado()==2) {
						estado = "Atraso";
					}else if(prestamoDetalle.getEstado()==1) {
						estado = "Saldo";
					}
					
					double cargoPagadoTemp = 0;
					double descuentoAdicionales = 0;
					double descuentoCargo = 0;
					
					if(!adicionales.isEmpty()) {
						for (PrestamoAdicional adicionalTemp : adicionales) {
							cargo+=adicionalTemp.getMonto()-adicionalTemp.getMonto_pagado();
							cargoPagadoTemp+=adicionalTemp.getMonto_pagado();
							descuentoAdicionales+=adicionalTemp.getDescuento_adicionales();
						}
						amortizacion.setCargo(cargo);
						descuentoCargo = amortizacion.getCargo()-descuentoAdicionales;
						amortizacion.setCargo(formato2d(cargo-descuentoAdicionales));
					}
					
					double abono = prestamoDetalle.getCapital_pagado()+prestamoDetalle.getInteres_pagado()+prestamoDetalle.getMora_pagada()+cargoPagadoTemp;
					
					double descuentoAdicional = 0;
					
					List<PrestamoAdicional> adicionalTemp = servicePrestamosAdicionales
							.buscarPorPrestamoNumeroCuota(prestamo, prestamoDetalle.getNumero());
					for (PrestamoAdicional adicional : adicionalTemp) {
						descuentoAdicional += adicional.getDescuento_adicionales();
					}

					amortizacion.setDescuento(formato2d(prestamoDetalle.getDescuento_mora()+descuentoAdicional));
					
					amortizacion.setAbono(formato2d(abono));
					amortizacion.setId(prestamoDetalle.getId());
					amortizacion.setFecha(sdf.format(prestamoDetalle.getFechaGenerada()));
					amortizacion.setCuota(prestamoDetalle.getCuota());
					amortizacion.setTipo(Integer.parseInt(prestamoDetalle.getPrestamo().getTipo()));
					amortizacion.setCapital(formato2d(prestamoDetalle.getCapital()-prestamoDetalle.getCapital_pagado()));
					amortizacion.setInteres(formato2d(prestamoDetalle.getInteres()-prestamoDetalle.getInteres_pagado()));
					amortizacion.setSaldo(formato2d(prestamoDetalle.getBalance()));
					amortizacion.setNumero(prestamoDetalle.getNumero());
					amortizacion.setMora(formato2d(prestamoDetalle.getMora()-prestamoDetalle.getMora_pagada()-prestamoDetalle.getDescuento_mora()));
					amortizacion.setEstado(prestamoDetalle.getEstado_cuota());
					amortizacion.setAtraso(prestamoDetalle.getDias_atraso());
					amortizacion.setBalance(formato2d((amortizacion.getCapital()==null?0:amortizacion.getCapital()) + (amortizacion.getInteres()==null?0:amortizacion.getInteres()) + (amortizacion.getMora()==null?0:amortizacion.getMora()) + (amortizacion.getCargo()==null?0:amortizacion.getCargo())));
					detalles.add(amortizacion);
				}
			}
			
			Integer idCliente; 
					
			if(session.getAttribute("cliente")!=null) {
				if((Integer) session.getAttribute("cliente") == 0) {
					idCliente = prestamo.getCliente().getId();
				}else {
					idCliente = (Integer) session.getAttribute("cliente");
				}
			}else {
				idCliente = prestamo.getCliente().getId();
			}

			Cliente cliente = serviceClientes.buscarPorId(idCliente);
			String datosCliente  = cliente.getNombre()+" / Tel.: "+cliente.getTelefono();

			if(prestamo.getTipo().equals("2")) {
				
				if(formato2d(sumaCargo) > 0 || formato2d(prestamo.getBalance()) > 0 || 
						formato2d(sumaInteresGen) > 0 || formato2d(sumaMora) > 0) {
					if(prestamo.getMoneda().equalsIgnoreCase("pesos")) {	
						PrestamoCalculoTotal prestamoCalculoTotal = new PrestamoCalculoTotal();
						prestamoCalculoTotal.setAdicionales(formato2d(sumaCargo));
						prestamoCalculoTotal.setCapital(formato2d(prestamo.getBalance()));
						prestamoCalculoTotal.setInteres(formato2d(sumaInteresGen));
						prestamoCalculoTotal.setMora(formato2d(sumaMora));
						prestamoCalculoTotal.setPrestamo(prestamo);
						prestamoCalculoTotal.setBalance(formato2d(sumaBalance));
						prestamoCalculoTotal.setMoneda("pesos");
						prestamosTotales.add(prestamoCalculoTotal);
						
						totalPesoAdicionales += formato2d(sumaCargo);
						totalPesoCapital += formato2d(prestamo.getBalance());
						totalPesoInteres += formato2d(sumaInteresGen);
						totalPesoMora += formato2d(sumaMora);
						
					}else if(prestamo.getMoneda().equalsIgnoreCase("dolar")) {
						PrestamoCalculoTotal prestamoCalculoTotal = new PrestamoCalculoTotal();
						prestamoCalculoTotal.setAdicionales(formato2d(sumaCargo));
						prestamoCalculoTotal.setCapital(formato2d(prestamo.getBalance()));
						prestamoCalculoTotal.setInteres(formato2d(sumaInteresGen));
						prestamoCalculoTotal.setMora(formato2d(sumaMora));
						prestamoCalculoTotal.setPrestamo(prestamo);
						prestamoCalculoTotal.setBalance(formato2d(sumaBalance));
						prestamoCalculoTotal.setMoneda("dolar");
						prestamosTotales.add(prestamoCalculoTotal);
						
						totalDolarAdicionales += formato2d(sumaCargo);
						totalDolarCapital += formato2d(prestamo.getBalance());
						totalDolarInteres += formato2d(sumaInteresGen);
						totalDolarMora += formato2d(sumaMora);
					}
					
					totalAdicionales += formato2d(sumaCargo);
					totalCapital += formato2d(prestamo.getBalance());
					totalInteres += formato2d(sumaInteresGen);
					totalMora += formato2d(sumaMora);
				}
			}

			double sumaCapital = 0;
			double montoCuota = 0;
			double sumaMoraTemp = 0;
			double sumaInteres = 0;
			double sumaCargos = 0;
			double sumaAbonoCuota = 0;
			double sumaDescuento = 0;
			double sumaTotales = 0;

			for (Amortizacion detalle : detalles) {
				sumaCapital+=detalle.getCapital()==null?0:detalle.getCapital();
				montoCuota+=detalle.getCuota()==null?0:detalle.getCuota();
				sumaMoraTemp+=detalle.getMora()==null?0:detalle.getMora();
				sumaInteres+=detalle.getInteres()==null?0:detalle.getInteres();
				sumaCargos+=detalle.getCargo()==null?0:detalle.getCargo();
				sumaAbonoCuota+=detalle.getAbono()==null?0:detalle.getAbono();
				sumaDescuento+=detalle.getDescuento()==null?0:detalle.getDescuento();
				sumaTotales+=detalle.getBalance()==null?0:detalle.getBalance();
			}

			if(!prestamo.getTipo().equals("2")) {
				if(formato2d(sumaCargos) > 0 || formato2d(sumaCapital) > 0 || 
						formato2d(sumaInteres) > 0 || formato2d(sumaMoraTemp) > 0) {
					if(prestamo.getMoneda().equalsIgnoreCase("pesos")) {		
						PrestamoCalculoTotal prestamoCalculoTotal = new PrestamoCalculoTotal();
						prestamoCalculoTotal.setAdicionales(formato2d(sumaCargos));
						prestamoCalculoTotal.setCapital(formato2d(sumaCapital));
						prestamoCalculoTotal.setInteres(formato2d(sumaInteres));
						prestamoCalculoTotal.setMora(formato2d(sumaMoraTemp));
						prestamoCalculoTotal.setPrestamo(prestamo);
						prestamoCalculoTotal.setBalance(formato2d(sumaTotales));
						prestamoCalculoTotal.setMoneda("pesos");
						prestamosTotales.add(prestamoCalculoTotal);
						
						totalPesoAdicionales += formato2d(sumaCargos);
						totalPesoCapital += formato2d(sumaCapital);
						totalPesoInteres += formato2d(sumaInteres);
						totalPesoMora += formato2d(sumaMoraTemp);
						
					}else if(prestamo.getMoneda().equalsIgnoreCase("dolar")) {
						PrestamoCalculoTotal prestamoCalculoTotal = new PrestamoCalculoTotal();
						prestamoCalculoTotal.setAdicionales(formato2d(sumaCargos));
						prestamoCalculoTotal.setCapital(formato2d(sumaCapital));
						prestamoCalculoTotal.setInteres(formato2d(sumaInteres));
						prestamoCalculoTotal.setMora(formato2d(sumaMoraTemp));
						prestamoCalculoTotal.setPrestamo(prestamo);
						prestamoCalculoTotal.setBalance(formato2d(sumaTotales));
						prestamoCalculoTotal.setMoneda("dolar");
						prestamosTotales.add(prestamoCalculoTotal);
						
						totalDolarAdicionales += formato2d(sumaCargos);
						totalDolarCapital += formato2d(sumaCapital);
						totalDolarInteres += formato2d(sumaInteres);
						totalDolarMora += formato2d(sumaMoraTemp);
					}
					
					totalAdicionales += formato2d(sumaCargos);
					totalCapital += formato2d(sumaCapital);
					totalInteres += formato2d(sumaInteres);
					totalMora += formato2d(sumaMoraTemp);
				}
			}					
		}
		
		reusableTotalCapital = moneda.equalsIgnoreCase("pesos")?formato2d(totalPesoCapital): formato2d(totalDolarCapital);
		reusableTotalInteres = moneda.equalsIgnoreCase("pesos")?formato2d(totalPesoInteres): formato2d(totalDolarInteres);
		reusableTotalMora = moneda.equalsIgnoreCase("pesos")?formato2d(totalPesoMora): formato2d(totalDolarMora);
		reusableTotalAdicional = moneda.equalsIgnoreCase("pesos")?formato2d(totalPesoAdicionales): formato2d(totalDolarAdicionales);
		
		model.addAttribute("totalCapital", reusableTotalCapital);
		model.addAttribute("totalInteres", reusableTotalInteres);
		model.addAttribute("totalMora", reusableTotalMora);
		model.addAttribute("totalAdicional", reusableTotalAdicional);
		
		model.addAttribute("prestamosTotales", prestamosTotales);
		return "impresiones/prestamos/listaPendientes :: listaPendientes";
	}	
	
//	@GetMapping("/pagosCompra")
//	public String reportePagosCompra(Model model, HttpSession session) {
//		return "impresiones/contabilidad/listaCompraPagos :: ";
//	}
	
	@GetMapping("/listaPendientes")
	public String listaPendientes(Model model, HttpSession session) throws ParseException {
		model.addAttribute("empresa", (Empresa) session.getAttribute("empresa"));
		model.addAttribute("prestamosTotales", prestamosTotales);
		model.addAttribute("totalCapital", reusableTotalCapital);
		model.addAttribute("totalInteres", reusableTotalInteres);
		model.addAttribute("totalMora", reusableTotalMora);
		model.addAttribute("totalAdicional", reusableTotalAdicional);
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
			String hasta, Integer idCliente, String moneda) throws ParseException {
		
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
		
		List<Abono> listaAbono = new LinkedList<>();
		
		for (Abono abono : abonos) {
			if(abono.getPrestamo().getMoneda().equalsIgnoreCase(moneda)) {
				listaAbono.add(abono);
			}
		}
		
		model.addAttribute("empresa", empresa);
		model.addAttribute("abonos", listaAbono);
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
	
	public double diasVencidos(LocalDateTime fecha1, LocalDateTime fecha2) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
         String fecha1Temp = fecha1.getDayOfMonth()+"/"+fecha1.getMonthValue()+"/"+fecha1.getYear();
         String fecha2Temp = fecha2.getDayOfMonth()+"/"+fecha2.getMonthValue()+"/"+fecha2.getYear();
         Date dt1 = sdf.parse(fecha1Temp);
         Date dt2 = sdf.parse(fecha2Temp);
         long diff = dt2.getTime() - dt1.getTime();
         return diff / 1000L / 60L / 60L / 24L;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder webDataBinder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}
}
