package com.contable.cron;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.contable.model.Amortizacion;
import com.contable.model.Carpeta;
import com.contable.model.Cliente;
import com.contable.model.Empresa;
import com.contable.model.EntradaIngresoContable;
import com.contable.model.FormaPago;
import com.contable.model.Prestamo;
import com.contable.model.PrestamoAdicional;
import com.contable.model.PrestamoCalculoTotal;
import com.contable.model.PrestamoDetalle;
import com.contable.model.PrestamoInteresDetalle;
import com.contable.model.RegistroCron;
import com.contable.service.IEmpresasService;
import com.contable.service.IEntradasIngresosContableService;
import com.contable.service.IFormasPagosService;
import com.contable.service.IPrestamosAdicionalesService;
import com.contable.service.IPrestamosDetallesService;
import com.contable.service.IPrestamosInteresesDetallesService;
import com.contable.service.IPrestamosService;

@Component
public class PrestamosCron {

	@Autowired
	private IPrestamosDetallesService servicePrestamosDetalles;
	
	@Autowired
	private IPrestamosInteresesDetallesService servicePrestamosInteresesDetalles;
	
	@Autowired
	private IPrestamosService servicePrestamos;
	
	@Autowired
	private IEmpresasService serviceEmpresas;
	
	@Autowired
	private IEntradasIngresosContableService serviceEntradasIngresosContables;
	
	@Autowired
	private IFormasPagosService serviceFormasPagos;
	
	@Autowired
	private IPrestamosAdicionalesService servicePrestamosAdicionales;
	
	private final Integer NORMAL = 0;
	private final Integer PAGADO = 1;
	private final Integer VENCIDO = 2;
	private final Integer LEGAL = 3;

	@Scheduled(cron = "0 00 03 * * *")
	public void calculoVencimientoCuota() throws ParseException {		
		//Buscamos los detalles pendientes de los prestamos 
		List<PrestamoDetalle> prestamoDetallesTemp = servicePrestamosDetalles.buscarPorEstado(0);
		if(!prestamoDetallesTemp.isEmpty()) {
			for (PrestamoDetalle prestamoDetalleTemp : prestamoDetallesTemp) {
				//verificamos si pasaron los dias de gracia
				LocalDateTime fecha = convertToLocalDateTimeViaInstant(prestamoDetalleTemp.getFechaGenerada());
				LocalDateTime fechaAcct =  LocalDateTime.now();
				if(fecha.isBefore(fechaAcct) || fecha.isEqual(fechaAcct)) {
					prestamoDetalleTemp.setEstado(2);
					servicePrestamosDetalles.guardar(prestamoDetalleTemp);
				}
			}
		}
		
		Double contMoraPeso = 0.0;
		Double contMoraDolar = 0.0;

		List<RegistroCron> registrosCron = new LinkedList<>();
		
		List<Empresa> empresas = serviceEmpresas.buscarTodas();
		
		for (Empresa empresa : empresas) {
			RegistroCron registroXEmpresa = new RegistroCron();
			registroXEmpresa.setMora(0.0);
			registroXEmpresa.setMoneda("pesos");
			registroXEmpresa.setEmpresa(empresa);
			registrosCron.add(registroXEmpresa);
			
			RegistroCron registroXEmpresa2 = new RegistroCron();
			registroXEmpresa2.setMora(0.0);
			registroXEmpresa2.setMoneda("dolar");
			registroXEmpresa2.setEmpresa(empresa);
			registrosCron.add(registroXEmpresa2);
		}
		
		List<PrestamoDetalle> prestamoDetalles = servicePrestamosDetalles.buscarPorEstado(2);
		if(!prestamoDetalles.isEmpty()) {
			for (PrestamoDetalle prestamoDetalle : prestamoDetalles) {
				//verificamos si pasaron los dias de gracia
				LocalDateTime fecha = convertToLocalDateTimeViaInstant(prestamoDetalle.getFechaGenerada());
				//Le sumamos los dias de gracia
				LocalDateTime fechaMax = fecha.plusDays(prestamoDetalle.getPrestamo().getDias_gracia());
				LocalDateTime fechaAcct =  LocalDateTime.now();
				
				//Verificamos si se le puede generar mora
				if(fechaAcct.isAfter(fechaMax) || fechaAcct.isEqual(fechaMax)) {
					//valor_cuota x valor_interes_mora divido/30 x dias_vencidos
					double vencidos = diasVencidos(fecha, fechaAcct);
					
					Double mora = 0.0;
					
					//Calculamos los dias vencidos despues de los dias de gracia
					mora = ((prestamoDetalle.getCuota() * (prestamoDetalle.getInteres_mora()/100) ) / 30.00) * vencidos;
					prestamoDetalle.setMora(formato2d(mora));
					prestamoDetalle.setDias_atraso((int) vencidos);
					if(vencidos < 90) {
						prestamoDetalle.setEstado(2);
						prestamoDetalle.setEstado_cuota("Atraso");
					}else {
						prestamoDetalle.setEstado(3);
						prestamoDetalle.setEstado_cuota("Legal");
					}
					servicePrestamosDetalles.guardar(prestamoDetalle);
					
					if(mora > 0) {
						if(prestamoDetalle.getPrestamo().getMoneda().equalsIgnoreCase("pesos")) {
							contMoraPeso += mora;
						}else if(prestamoDetalle.getPrestamo().getMoneda().equalsIgnoreCase("dolar")){
							contMoraDolar += mora;
						}

						for (RegistroCron registroCron : registrosCron) {
							if((registroCron.getEmpresa().getId().intValue() == prestamoDetalle.getPrestamo().getEmpresa().getId().intValue()) && 
									prestamoDetalle.getPrestamo().getMoneda().equalsIgnoreCase(registroCron.getMoneda())) {
								registroCron.setMora(registroCron.getMora() + mora);
								registroCron.setPrestamo(prestamoDetalle.getPrestamo());
							}
						}
					}
				}
			}
							
 			List<FormaPago> formasPagoPeso = new LinkedList<>();
 			List<FormaPago> formasPagoDolar = new LinkedList<>();
 			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
 				
 			for (Empresa empresa : empresas) {
 				
 				List<EntradaIngresoContable> entradasIngresosContablesPeso = new LinkedList<>();

 				if(contMoraPeso.doubleValue()>0) {
 								
	 				formasPagoPeso = serviceFormasPagos.buscarPorEmpresaIdentificadorTasaCambio(empresa, "enlaceMora", "peso");
	 						 						
	 	 			if(!formasPagoPeso.isEmpty()) {
	 	 				
						entradasIngresosContablesPeso = serviceEntradasIngresosContables.buscarPorEmpresaCuentaContable(empresa, formasPagoPeso.get(0).getCuentaContable());
 	 					EntradaIngresoContable entradaContablePeso = new EntradaIngresoContable();
 	 					
 	 					if(!entradasIngresosContablesPeso.isEmpty()) {
 	 						//Buscamos el ultimo registro de la cuenta contable
 	 						entradaContablePeso = entradasIngresosContablesPeso.get(entradasIngresosContablesPeso.size()-1);
 	 					}
 	 					
	 	 			    //Ingreso contable de mora pesos
 	 					if(formasPagoPeso.get(0).getCuentaContable()!=null) {
 			 	 			EntradaIngresoContable entradaIngresoContableMoraPeso = new EntradaIngresoContable();
 			 	 			if(!formasPagoPeso.isEmpty()) {
 			 	 				entradaIngresoContableMoraPeso.setCuentaContable(formasPagoPeso.get(0).getCuentaContable());
 			 	 			}else {
 			 	 				entradaIngresoContableMoraPeso.setCuentaContable(null);
 			 	 			}
 			 	 			
 			 	 			Double montoAcumuladoMora = calculoTotalMoraAcumuladaXCobrar(empresa, "pesos");
 						
 			 	 			if(entradaContablePeso.getId()!=null) {
 			 	 				entradaIngresoContableMoraPeso.setTotal(formato2d(montoAcumuladoMora) - entradaContablePeso.getBalanceContable());
 			 	 				entradaIngresoContableMoraPeso.setBalance(formato2d(montoAcumuladoMora) - entradaContablePeso.getBalanceContable());
 			 	 			}
 			 	 			
 			 	 			entradaIngresoContableMoraPeso.setEmpresa(empresa);
 			 	 			entradaIngresoContableMoraPeso.setFecha(new Date());
 			 	 			entradaIngresoContableMoraPeso.setInfo("moras generadas a clientes de la fecha "+sdf.format(new Date()));
 		 	 	 			serviceEntradasIngresosContables.guardar(entradaIngresoContableMoraPeso);
 	 					}
	 	 			}
 	 			}

 					
 				if(contMoraDolar.doubleValue()>0) {
 					formasPagoDolar = serviceFormasPagos.buscarPorEmpresaIdentificadorTasaCambio(empresa, "enlaceMora", "dolar");
 					
 					List<EntradaIngresoContable> entradasIngresosContablesDolar = new LinkedList<>();
 					
 					if(!formasPagoDolar.isEmpty()) {
 						entradasIngresosContablesDolar = serviceEntradasIngresosContables.buscarPorEmpresaCuentaContable(empresa, formasPagoDolar.get(0).getCuentaContable());
 	 					EntradaIngresoContable entradaContableDolar = new EntradaIngresoContable();
 	 					
 	 					if(!entradasIngresosContablesDolar.isEmpty()) {
 	 						//Buscamos el ultimo registro de mora generado por sistem para actualizar el monto de la mora
 	 						entradaContableDolar = entradasIngresosContablesDolar.get(entradasIngresosContablesDolar.size()-1);
 	 					}
 	 					
 	 	 				//Ingreso contable de mora dolar
 	 					if(formasPagoDolar.get(0).getCuentaContable()!=null) {
 		 	 				EntradaIngresoContable entradaIngresoContableDolar = new EntradaIngresoContable();
 	 	 	 				if(!formasPagoPeso.isEmpty()) {
 	 	 	 					entradaIngresoContableDolar.setCuentaContable(formasPagoDolar.get(0).getCuentaContable());
 	 	 	 				}else {
 	 	 	 					entradaIngresoContableDolar.setCuentaContable(null);
 	 	 	 				}
 	 	 	 				
 	 	 	 				Double montoAcumuladoMora = calculoTotalMoraAcumuladaXCobrar(empresa, "dolar");
 	 	 	 					
 	 	 	 				if(entradaContableDolar.getId()!=null) {
 	 	 	 	 				entradaIngresoContableDolar.setTotal(formato2d(montoAcumuladoMora) - formato2d(entradaContableDolar.getBalanceContable()));
 	 	 	 	 				entradaIngresoContableDolar.setBalance(formato2d(montoAcumuladoMora) - formato2d(entradaContableDolar.getBalanceContable()));
 	 	 	 				}
 	 	 	 				
 	 	 	 				entradaIngresoContableDolar.setEmpresa(empresa);
 	 	 	 				entradaIngresoContableDolar.setFecha(new Date());
 	 	 	 				entradaIngresoContableDolar.setInfo("moras generadas a clientes de la fecha "+sdf.format(new Date()));
 	 	 	 					
 	 	 	 				if(!formasPagoPeso.isEmpty()) {
 	 	 	 	 				serviceEntradasIngresosContables.guardar(entradaIngresoContableDolar);
 	 	 	 				}
 	 					}
 					}
 				}
 			}

				for (Empresa empresa : empresas) {
					//Actualizacion contable
					// Buscamos las entradas ingresos contables null ASCENDENTE
					List<EntradaIngresoContable> entradasIngresosContablesNullTemp = serviceEntradasIngresosContables
							.buscarPorEmpresaBalanceContableNullASC(empresa);

					if (!entradasIngresosContablesNullTemp.isEmpty()) {
						for (EntradaIngresoContable entradaIngresoContableNull : entradasIngresosContablesNullTemp) {
							double balanceContableTemp = 0;
							// Buscamos las entradas ingresos contables anteriores con la cuenta contable de
							// la iteracion
							List<EntradaIngresoContable> entradasIngresosContablesXCCNotNUll = serviceEntradasIngresosContables
									.buscarPorEmpresaCuentaContableBalanceContableNotNullMenorQueID(empresa,
											entradaIngresoContableNull.getCuentaContable(), entradaIngresoContableNull.getId());
							if (!entradasIngresosContablesXCCNotNUll.isEmpty()) {
								for (EntradaIngresoContable entradaIngresoContableNotNull : entradasIngresosContablesXCCNotNUll) {
									balanceContableTemp = entradaIngresoContableNotNull.getBalanceContable() == null ? 0
											: entradaIngresoContableNotNull.getBalanceContable();
									break;
								}
								entradaIngresoContableNull.setBalanceContableInicial(balanceContableTemp);
								entradaIngresoContableNull.setBalanceContable(entradaIngresoContableNull.getBalanceContableInicial()
										+ entradaIngresoContableNull.getBalance());
								serviceEntradasIngresosContables.guardar(entradaIngresoContableNull);
							} else {
								entradaIngresoContableNull.setBalanceContableInicial(balanceContableTemp);
								entradaIngresoContableNull.setBalanceContable(entradaIngresoContableNull.getBalanceContableInicial()
										+ entradaIngresoContableNull.getBalance());
								serviceEntradasIngresosContables.guardar(entradaIngresoContableNull);
							}
						}
					}
				}
		}
	}
	
	private Double calculoTotalMoraAcumuladaXCobrar(Empresa empresa, String moneda) throws ParseException {
				
		double totalPesoAdicionales = 0;
		double totalPesoCapital = 0;
		double totalPesoInteres = 0;
		double totalPesoMora = 0;
		
		double totalDolarAdicionales = 0;
		double totalDolarCapital = 0;
		double totalDolarInteres = 0;
		double totalDolarMora = 0;
		
		double totalAdicionales = 0;
		double totalCapital = 0;
		double totalInteres = 0;
		double totalMora = 0;
		
		List<PrestamoCalculoTotal> prestamosTotales = new LinkedList<>();
		
		List<Integer> estados = new LinkedList<>();
		estados.add(1);
		
		List<Prestamo> prestamos = servicePrestamos.buscarPorEmpresaMonedaEstadoNotIn(empresa, moneda, estados);
		
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
					amortizacion.setMora(formato2d(formato2d(prestamoDetalle.getMora())-formato2d(prestamoDetalle.getMora_pagada())-formato2d(prestamoDetalle.getDescuento_mora())));
					amortizacion.setEstado(prestamoDetalle.getEstado_cuota());
					amortizacion.setAtraso(prestamoDetalle.getDias_atraso());
					amortizacion.setBalance(formato2d((amortizacion.getCapital()==null?0:amortizacion.getCapital()) + (amortizacion.getInteres()==null?0:amortizacion.getInteres()) + (amortizacion.getMora()==null?0:amortizacion.getMora()) + (amortizacion.getCargo()==null?0:amortizacion.getCargo())));
					detalles.add(amortizacion);
				}
			}

			if(prestamo.getTipo().equals("2")) {
				
				if(formato2d(sumaCargo) > 0 || formato2d(prestamo.getBalance()) > 0 
						|| formato2d(sumaInteresGen) > 0 || formato2d(sumaMora) > 0 ) {
					
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
		
		double monto = 0;
		
		if(moneda.equalsIgnoreCase("pesos")) {
			monto = formato2d(totalPesoMora);
		}else if(moneda.equalsIgnoreCase("dolar")) {
			monto = formato2d(totalDolarMora);
		}

		return monto;
	}

	@Scheduled(cron = "0 10 03 * * *")
	public void generarPrestamoInteresDetalle() throws ParseException {
		List<Prestamo> prestamos = servicePrestamos.buscarPorEstado(NORMAL);
		LocalDateTime dateAcct =  LocalDateTime.now();
		LocalDateTime fechaCron = null;
		
		double contInteresPeso = 0.0;
		double contInteresDolar = 0.0;
		
		Double contMoraPeso = 0.0;
		Double contMoraDolar = 0.0;

		List<RegistroCron> registrosCron = new LinkedList<>();
		
		List<Empresa> empresas = serviceEmpresas.buscarTodas();
		
		for (Empresa empresa : empresas) {
			RegistroCron registroXEmpresa = new RegistroCron();
			registroXEmpresa.setMora(0.0);
			registroXEmpresa.setMoneda("pesos");
			registroXEmpresa.setEmpresa(empresa);
			registrosCron.add(registroXEmpresa);
			
			RegistroCron registroXEmpresa2 = new RegistroCron();
			registroXEmpresa2.setMora(0.0);
			registroXEmpresa2.setMoneda("dolar");
			registroXEmpresa2.setEmpresa(empresa);
			registrosCron.add(registroXEmpresa2);
		}

		for (Prestamo prestamo : prestamos) {
			int count = 0;
			prestamo = servicePrestamos.buscarPorId(prestamo.getId());
			if(prestamo.getEstado()!=1) {
				fechaCron = convertToLocalDateTimeViaInstant(prestamo.getFecha_cron());
				while (fechaCron.isBefore(dateAcct)) {
					fechaCron = convertToLocalDateTimeViaInstant(prestamo.getFecha_cron());
					if (fechaCron.isBefore(dateAcct)) {
						double monto = prestamo.getBalance() * (prestamo.getTasa() / 100);
						PrestamoInteresDetalle prestamoInteresDetalle = new PrestamoInteresDetalle();
						count++;
						
						prestamoInteresDetalle.setInteres(monto);
						prestamoInteresDetalle.setTasa(prestamo.getMora());
						
						if(monto>0) {
							if(prestamo.getMoneda().equalsIgnoreCase("pesos")) {
								contInteresPeso += monto;
							}else if(prestamo.getMoneda().equalsIgnoreCase("dolar")) {
								contInteresDolar += monto;
							}
							
						}

						LocalDateTime fechaCuotaTemp = convertToLocalDateTimeViaInstant(prestamo.getFecha_cron())
								.plusMonths(1).minusDays(prestamo.getDias_gracia());
						Date fechaCuota = convertToDateViaInstant(fechaCuotaTemp);

						prestamoInteresDetalle.setFecha_cuota(fechaCuota);
						prestamoInteresDetalle.setFecha(new Date());

						LocalDateTime fechaVencimientoTemp = convertToLocalDateTimeViaInstant(prestamo.getFecha_cron())
								.plusMonths(1);
						Date fechaVencimiento = convertToDateViaInstant(fechaVencimientoTemp);

						prestamoInteresDetalle.setVencimiento(fechaVencimiento);
						prestamoInteresDetalle.setPrestamo(prestamo);
						prestamoInteresDetalle.setNumero_cuota(count);
						
						servicePrestamosInteresesDetalles.guardar(prestamoInteresDetalle);

						// Actualizamos la fecha cron del prestamo a 1 mes
						LocalDateTime fechaCronPrestamoTemp = convertToLocalDateTimeViaInstant(prestamo.getFecha_cron())
								.plusMonths(1);
						Date fechaCronPrestamo = convertToDateViaInstant(fechaCronPrestamoTemp);
						prestamo.setFecha_cron(fechaCronPrestamo);
						servicePrestamos.guardar(prestamo);
					}
				}
			}
		}
		
		//Ingresos de intereses
		
		if(contInteresPeso>0 || contInteresDolar>0) {
			
			List<FormaPago> formasPagoPeso = new LinkedList<>();
			List<FormaPago> formasPagoDolar = new LinkedList<>();
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
				
				for (Empresa empresa : empresas) { 					
					if(contInteresPeso>0) {
						formasPagoPeso = serviceFormasPagos.buscarPorEmpresaIdentificadorTasaCambio(empresa, "enlaceInteres", "peso");
						 						
	 					//Ingreso contable de interes pesos
	 					EntradaIngresoContable entradaIngresoContableInteresesIngreso = new EntradaIngresoContable();
	 					if(!formasPagoPeso.isEmpty()) {
	 						entradaIngresoContableInteresesIngreso.setCuentaContable(formasPagoPeso.get(0).getCuentaContable());
	 					}else {
	 						entradaIngresoContableInteresesIngreso.setCuentaContable(null);
	 					}
	 					 	 					
	 					entradaIngresoContableInteresesIngreso.setEmpresa(empresa);
	 					entradaIngresoContableInteresesIngreso.setFecha(new Date());
	 					entradaIngresoContableInteresesIngreso.setTotal(formato2d(contInteresPeso));
	 					entradaIngresoContableInteresesIngreso.setBalance(formato2d(contInteresPeso));
	 					entradaIngresoContableInteresesIngreso.setInfo("intereses generados por prestamos de la fecha "+sdf.format(new Date()));
	 					
	 					if(!formasPagoPeso.isEmpty()) {
	 	 					serviceEntradasIngresosContables.guardar(entradaIngresoContableInteresesIngreso);
	 					}
	 				}

					if(contInteresDolar>0) {
						formasPagoDolar = serviceFormasPagos.buscarPorEmpresaIdentificadorTasaCambio(empresa, "enlaceInteres", "dolar");
						
	 					//Ingreso contable de interes dolar
	 					EntradaIngresoContable entradaIngresoContableAdicionalesDIngreso = new EntradaIngresoContable();
	 					if(!formasPagoPeso.isEmpty()) {
	 						entradaIngresoContableAdicionalesDIngreso.setCuentaContable(formasPagoDolar.get(0).getCuentaContable());
	 					}else {
	 						entradaIngresoContableAdicionalesDIngreso.setCuentaContable(null);

	 					}
	 			
	 					entradaIngresoContableAdicionalesDIngreso.setEmpresa(empresa);
	 					entradaIngresoContableAdicionalesDIngreso.setFecha(new Date());
	 					entradaIngresoContableAdicionalesDIngreso.setTotal(formato2d(contInteresDolar));
	 					entradaIngresoContableAdicionalesDIngreso.setBalance(formato2d(contInteresDolar));
	 					entradaIngresoContableAdicionalesDIngreso.setInfo("intereses generados por prestamos de la fecha "+sdf.format(new Date()));
	 					
	 					if(!formasPagoDolar.isEmpty()) {
	 	 					serviceEntradasIngresosContables.guardar(entradaIngresoContableAdicionalesDIngreso);
	 					}
					}

				}

			for (Empresa empresa : empresas) {
				//Actualizacion contable
				// Buscamos las entradas ingresos contables null ASCENDENTE
				List<EntradaIngresoContable> entradasIngresosContablesNullTemp = serviceEntradasIngresosContables
						.buscarPorEmpresaBalanceContableNullASC(empresa);

				if (!entradasIngresosContablesNullTemp.isEmpty()) {
					for (EntradaIngresoContable entradaIngresoContableNull : entradasIngresosContablesNullTemp) {
						double balanceContableTemp = 0;
						// Buscamos las entradas ingresos contables anteriores con la cuenta contable de
						// la iteracion
						List<EntradaIngresoContable> entradasIngresosContablesXCCNotNUll = serviceEntradasIngresosContables
								.buscarPorEmpresaCuentaContableBalanceContableNotNullMenorQueID(empresa,
										entradaIngresoContableNull.getCuentaContable(), entradaIngresoContableNull.getId());
						if (!entradasIngresosContablesXCCNotNUll.isEmpty()) {
							for (EntradaIngresoContable entradaIngresoContableNotNull : entradasIngresosContablesXCCNotNUll) {
								balanceContableTemp = entradaIngresoContableNotNull.getBalanceContable() == null ? 0
										: entradaIngresoContableNotNull.getBalanceContable();
								break;
							}
							entradaIngresoContableNull.setBalanceContableInicial(balanceContableTemp);
							entradaIngresoContableNull.setBalanceContable(entradaIngresoContableNull.getBalanceContableInicial()
									+ entradaIngresoContableNull.getBalance());
							serviceEntradasIngresosContables.guardar(entradaIngresoContableNull);
						} else {
							entradaIngresoContableNull.setBalanceContableInicial(balanceContableTemp);
							entradaIngresoContableNull.setBalanceContable(entradaIngresoContableNull.getBalanceContableInicial()
									+ entradaIngresoContableNull.getBalance());
							serviceEntradasIngresosContables.guardar(entradaIngresoContableNull);
						}
					}
				}
			}
		
		}
		
		List<PrestamoInteresDetalle> prestamoInteresDetalles = servicePrestamosInteresesDetalles.buscarPorEstado(NORMAL);
		
		for (PrestamoInteresDetalle prestamoInteresDetalle : prestamoInteresDetalles) {
			fechaCron = convertToLocalDateTimeViaInstant(prestamoInteresDetalle.getPrestamo().getFecha_cron());
			Date fecha = prestamoInteresDetalle.getFecha_cuota();
			
			if(fechaCron.isAfter(dateAcct)) {

				LocalDateTime fechaTemp = convertToLocalDateTimeViaInstant(fecha);
				double vencidos = diasVencidos(fechaTemp, dateAcct);
				
				//Calculamos los dias vencidos despues de los dias de gracia
				if(vencidos<0.0) {
					vencidos = 0;
				}
				
				prestamoInteresDetalle.setDias_atraso((int) vencidos);
				
				if(vencidos>0) {
					if(vencidos>=90) {
						prestamoInteresDetalle.setEstado(LEGAL);
					}else {
						prestamoInteresDetalle.setEstado(VENCIDO);
					}
					Double mora = ((prestamoInteresDetalle.getInteres() * (prestamoInteresDetalle.getPrestamo().getMora()/100) ) / 30.00) * vencidos;
					prestamoInteresDetalle.setMora(mora);
					
					if(mora > 0) {
						if(prestamoInteresDetalle.getPrestamo().getMoneda().equalsIgnoreCase("pesos")) {
							contMoraPeso += formato2d(mora);
						}else if(prestamoInteresDetalle.getPrestamo().getMoneda().equalsIgnoreCase("dolar")){
							contMoraDolar += formato2d(mora);
						}

						for (RegistroCron registroCron : registrosCron) {
							if((registroCron.getEmpresa().getId().intValue() == prestamoInteresDetalle.getPrestamo().getEmpresa().getId().intValue()) && 
									prestamoInteresDetalle.getPrestamo().getMoneda().equalsIgnoreCase(registroCron.getMoneda())) {
								registroCron.setMora(registroCron.getMora() + mora);
								registroCron.setPrestamo(prestamoInteresDetalle.getPrestamo());
							}
						}
					}
				}

				servicePrestamosInteresesDetalles.guardar(prestamoInteresDetalle);
			}
		}
		
		List<FormaPago> formasPagoPeso = new LinkedList<>();
		List<FormaPago> formasPagoDolar = new LinkedList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		
		for (Empresa empresa : empresas) { 	
				
			double temporalSumaMoraPesos = 0;
			double temporalSumaMoraDolar = 0;

			if(contMoraPeso.doubleValue()>0) {
					
	 			for (RegistroCron registroCron : registrosCron) {
	 				if(empresa.getId().intValue() == registroCron.getEmpresa().getId().intValue() && 
	 						registroCron.getMoneda().equalsIgnoreCase("pesos")) {
	 					temporalSumaMoraPesos+=formato2d(registroCron.getMora());
	 				}else if(empresa.getId().intValue() == registroCron.getEmpresa().getId().intValue() && 
	 						registroCron.getMoneda().equalsIgnoreCase("dolar")) {
	 					temporalSumaMoraDolar+=formato2d(registroCron.getMora());
	 				}
	 			}
					
 				formasPagoPeso = serviceFormasPagos.buscarPorEmpresaIdentificadorTasaCambio(empresa, "enlaceMora", "peso");
 						 
 				List<EntradaIngresoContable> entradasIngresosContablesPeso = new LinkedList<>();
					
				if (!formasPagoPeso.isEmpty()) {
					entradasIngresosContablesPeso = serviceEntradasIngresosContables
							.buscarPorEmpresaCuentaContable(empresa, formasPagoPeso.get(0).getCuentaContable());
					EntradaIngresoContable entradaContablePeso = new EntradaIngresoContable();

					if (!entradasIngresosContablesPeso.isEmpty()) {
						// Buscamos el ultimo registro de mora generado por sistem para actualizar el
						// monto de la mora
						entradaContablePeso = entradasIngresosContablesPeso
								.get(entradasIngresosContablesPeso.size() - 1);
					}

					// Ingreso contable de mora peso
					EntradaIngresoContable entradaIngresoContablePeso = new EntradaIngresoContable();
					if (!formasPagoPeso.isEmpty()) {
						entradaIngresoContablePeso.setCuentaContable(formasPagoDolar.get(0).getCuentaContable());
					} else {
						entradaIngresoContablePeso.setCuentaContable(null);
					}
					
					Double montoAcumuladoMora = calculoTotalMoraAcumuladaXCobrar(empresa, "pesos");

					if (entradaContablePeso.getId() != null) {
						entradaIngresoContablePeso.setTotal(formato2d(montoAcumuladoMora) - formato2d(entradaContablePeso.getBalanceContable()));
						entradaIngresoContablePeso.setBalance(formato2d(montoAcumuladoMora) - formato2d(entradaContablePeso.getBalanceContable()));
					}
					
					entradaIngresoContablePeso.setEmpresa(empresa);
					entradaIngresoContablePeso.setFecha(new Date());
					entradaIngresoContablePeso.setInfo("moras generadas a clientes de la fecha "+sdf.format(new Date()));
					
	 	 			if(!formasPagoPeso.isEmpty()) {
	 	 	 			serviceEntradasIngresosContables.guardar(entradaIngresoContablePeso);
	 	 			}
				}
			}
				
			if(contMoraDolar.doubleValue()>0) {
				
				List<EntradaIngresoContable> entradasIngresosContablesDolar = new LinkedList<>();
				
				formasPagoDolar = serviceFormasPagos.buscarPorEmpresaIdentificadorTasaCambio(empresa, "enlaceMora", "dolar");
						
	 			if(!formasPagoDolar.isEmpty()) {
	 				//Ingreso contable de mora dolar
	 				entradasIngresosContablesDolar = serviceEntradasIngresosContables
							.buscarPorEmpresaCuentaContable(empresa, formasPagoDolar.get(0).getCuentaContable());
					EntradaIngresoContable entradaContableDolar = new EntradaIngresoContable();

					if (!entradasIngresosContablesDolar.isEmpty()) {
						// Buscamos el ultimo registro de mora generado por sistem para actualizar el
						// monto de la mora
						entradaContableDolar = entradasIngresosContablesDolar.get(entradasIngresosContablesDolar.size() - 1);
					}
	 				
		 			EntradaIngresoContable entradaIngresoContableDolar = new EntradaIngresoContable();
		 			if(!formasPagoPeso.isEmpty()) {
		 				entradaIngresoContableDolar.setCuentaContable(formasPagoDolar.get(0).getCuentaContable());
		 			}else {
		 				entradaIngresoContableDolar.setCuentaContable(null);
		 			}
		 			
		 			Double montoAcumuladoMora = calculoTotalMoraAcumuladaXCobrar(empresa, "dolar");
		 			
					if (entradaContableDolar.getId() != null) {
						entradaIngresoContableDolar.setTotal(formato2d(montoAcumuladoMora) - formato2d(entradaContableDolar.getBalanceContable()));
						entradaIngresoContableDolar.setBalance(formato2d(montoAcumuladoMora) - formato2d(entradaContableDolar.getBalanceContable()));
					}
		 					
					entradaIngresoContableDolar.setEmpresa(empresa);
					entradaIngresoContableDolar.setFecha(new Date());
					entradaIngresoContableDolar.setInfo("moras generadas a clientes de la fecha "+sdf.format(new Date()));
	 	 			serviceEntradasIngresosContables.guardar(entradaIngresoContableDolar);
	 			}
			}
		}

			for (Empresa empresa : empresas) {
				//Actualizacion contable
				// Buscamos las entradas ingresos contables null ASCENDENTE
				List<EntradaIngresoContable> entradasIngresosContablesNullTemp = serviceEntradasIngresosContables
						.buscarPorEmpresaBalanceContableNullASC(empresa);

				if (!entradasIngresosContablesNullTemp.isEmpty()) {
					for (EntradaIngresoContable entradaIngresoContableNull : entradasIngresosContablesNullTemp) {
						double balanceContableTemp = 0;
						// Buscamos las entradas ingresos contables anteriores con la cuenta contable de
						// la iteracion
						List<EntradaIngresoContable> entradasIngresosContablesXCCNotNUll = serviceEntradasIngresosContables
								.buscarPorEmpresaCuentaContableBalanceContableNotNullMenorQueID(empresa,
										entradaIngresoContableNull.getCuentaContable(), entradaIngresoContableNull.getId());
					if (!entradasIngresosContablesXCCNotNUll.isEmpty()) {
						for (EntradaIngresoContable entradaIngresoContableNotNull : entradasIngresosContablesXCCNotNUll) {
							balanceContableTemp = entradaIngresoContableNotNull.getBalanceContable() == null ? 0
									: entradaIngresoContableNotNull.getBalanceContable();
							break;
						}
						entradaIngresoContableNull.setBalanceContableInicial(balanceContableTemp);
						entradaIngresoContableNull.setBalanceContable(entradaIngresoContableNull.getBalanceContableInicial()
								+ entradaIngresoContableNull.getBalance());
							serviceEntradasIngresosContables.guardar(entradaIngresoContableNull);
					} else {
							entradaIngresoContableNull.setBalanceContableInicial(balanceContableTemp);
							entradaIngresoContableNull.setBalanceContable(entradaIngresoContableNull.getBalanceContableInicial()
									+ entradaIngresoContableNull.getBalance());
						serviceEntradasIngresosContables.guardar(entradaIngresoContableNull);
					}
				}
			}
		}	
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
	
}
