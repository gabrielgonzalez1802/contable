package com.contable.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.contable.model.Amortizacion;
import com.contable.model.Cliente;
import com.contable.model.Prestamo;
import com.contable.service.IClientesService;
import com.contable.service.IPrestamosService;

@Controller
@RequestMapping("/prestamos")
public class PrestamosController {
	
	@Autowired
	private IPrestamosService servicePrestamos;
	
	@Autowired
	private IClientesService serviceClientes;
	
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	
	@GetMapping("/")
	public String listaPrestamos(Model model) {
		return "prestamos/listaPrestamos :: listaPrestamo";
	}
	
	@GetMapping("/agregar")
	public String agregarPrestamos(Model model) {
		Prestamo prestamo = new Prestamo();
		List<Cliente> clientes = serviceClientes.buscarPorEstado(1);
		model.addAttribute("clientes", clientes);
		model.addAttribute("prestamo", prestamo);
		return "prestamos/form :: form";
	}
	
	@PostMapping("/calculoCuota")
	public String calculoCuota(Model model, @ModelAttribute("prestamo") Prestamo prestamo, @RequestParam("fecha") String fecha) {
		int fre = 0;
		Double totalPagar  = 0.0;
		Double interes = prestamo.getTasa();
		Double cuota = 0.0;
		Double capital = 0.0;
		Double total_pagar = 0.0;
		
		Date date;
		try {
			date = formatter.parse(fecha);
			prestamo.setFecha(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		//Forma de pago
		if (prestamo.getForma_pago().equals("1")) {
			fre = 30;
		} else if (prestamo.getForma_pago().equals("2")) {
			fre = 15;
		} else if (prestamo.getForma_pago().equals("7")) {
			fre = 4;
		} else if (prestamo.getForma_pago().equals("15")) {
			fre = 2;
		} else if (prestamo.getForma_pago().equals("30")) {
			fre = 1;
		}
		
		//Tipos
		if(prestamo.getTipo().equals("1")) {
			//Coutas fijas
			interes = (prestamo.getMonto()*(prestamo.getTasa()/100)*(prestamo.getPagos()/fre))/prestamo.getPagos();

		    capital = prestamo.getMonto() / prestamo.getPagos();
		    cuota =   capital + interes;
		    total_pagar = cuota * prestamo.getPagos();
		
		}else if(prestamo.getTipo().equals("3")) {
			//Cuotas variables	
			  Double cant_prestada = prestamo.getMonto();
			  cuota = cant_prestada * (Math.pow((1+(prestamo.getTasa()/100)), prestamo.getPagos()) * (prestamo.getTasa()/100)) / (Math.pow((1+(prestamo.getTasa()/100)), prestamo.getPagos()) - 1);

			  Double pagoInteres = cant_prestada * (prestamo.getTasa()/100);
			  Double pagoCapital = cuota - pagoInteres;
			  cant_prestada-= pagoCapital;

			  interes=cuota; //pagoInteres -> Segun validaciones del front toma el mismo valor de la cuota
			  capital=pagoCapital;	
			  total_pagar = cuota * prestamo.getPagos();
		}
		
		model.addAttribute("capital", capital > 0 ? formato2d(capital) : capital);
		model.addAttribute("cuota", cuota > 0 ? formato2d(cuota) : cuota);
		model.addAttribute("interes", interes > 0 ? formato2d(interes) : interes);
		model.addAttribute("total_pagar", total_pagar > 0 ? formato2d(total_pagar) : total_pagar);
		return "prestamos/form :: #calculosCuota";
	}
	
	@PostMapping("/calculoTasa")
	public String calculoTasa(Model model, @ModelAttribute("prestamo") Prestamo prestamo, @RequestParam("fecha") String fecha) {
		int fre = 0;
		Double totalPagar  = 0.0;
		Double interes = prestamo.getTasa();
		Double cuota = prestamo.getValor_cuota();
		Double capital = 0.0;
		Double total_pagar = 0.0;
		
		Date date;
		try {
			date = formatter.parse(fecha);
			prestamo.setFecha(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		//Forma de pago
		if (prestamo.getForma_pago().equals("1")) {
			fre = 30;
		} else if (prestamo.getForma_pago().equals("2")) {
			fre = 15;
		} else if (prestamo.getForma_pago().equals("7")) {
			fre = 4;
		} else if (prestamo.getForma_pago().equals("15")) {
			fre = 2;
		} else if (prestamo.getForma_pago().equals("30")) {
			fre = 1;
		}
		
		capital = prestamo.getMonto() / prestamo.getPagos();
		interes = cuota - capital;
		total_pagar = cuota * prestamo.getPagos();
		
		Double f= (double) (prestamo.getPagos()/fre);
		
		Double tasa = ((100) * interes * prestamo.getPagos()) / (prestamo.getMonto() *(prestamo.getPagos()/fre)) ;
		
		cuota = capital + interes;

		
		model.addAttribute("capital", formato2d(capital));
		model.addAttribute("tasa", formato2d(tasa));
		model.addAttribute("interes", formato2d(interes));
		model.addAttribute("total_pagar", formato2d(total_pagar));
		return "prestamos/form :: #calculosTasa";
	}
	
	@PostMapping("/amortizar")
	public String amortizar(Model model, @ModelAttribute("prestamo") Prestamo prestamo) {
		int fre = 0;
		double total_cuota = 0;
		double total_capital = 0;
		double total_interes = 0;
		double total_neto = 0;

		List<Amortizacion> detalles = new LinkedList<>();

		if (prestamo.getPagos() != 0) {

			// Forma de pago
			if (prestamo.getForma_pago().equals("1")) {
				fre = 30;
			} else if (prestamo.getForma_pago().equals("2")) {
				fre = 15;
			} else if (prestamo.getForma_pago().equals("7")) {
				fre = 4;
			} else if (prestamo.getForma_pago().equals("15")) {
				fre = 2;
			} else if (prestamo.getForma_pago().equals("30")) {
				fre = 1;
			}
			
			Date date = new Date();
			LocalDate fecha_pagoTemp = date.toInstant().atZone(ZoneId.systemDefault().systemDefault())
					.toLocalDate();
			int mes = fecha_pagoTemp.getMonthValue();
			int agno = fecha_pagoTemp.getYear();
			String fecha_pago = fecha_pagoTemp.getDayOfMonth() + "-" + mes + "-" + agno;

			if (prestamo.getTipo().equals("1")) {
				//Coutas Fijas
				int x = 0;

				double interes = (prestamo.getMonto() * (prestamo.getTasa() / 100) * (prestamo.getPagos() / fre))
						/ prestamo.getPagos();

				double capital = prestamo.getMonto() / prestamo.getPagos();
				double cuota = capital + interes;
				double total_pagar = cuota * prestamo.getPagos();

				for (int i = 1; i <= prestamo.getPagos(); i++) {

					total_cuota += cuota;
					total_capital += capital;
					total_interes += interes;
					total_pagar = total_pagar - (cuota);

					if (prestamo.getForma_pago().equals("30")) {
						// *************************************************** */

						int dia = fecha_pagoTemp.getDayOfMonth();

						mes++;

						if (mes == 13) {
							mes = 1;
							agno++;
						}

						if (dia == 31 || (dia > 28 && mes == 2)) {

							Calendar calendar = Calendar.getInstance();
							calendar.set(Calendar.MONTH, mes);
							calendar.set(Calendar.YEAR, agno);
							calendar.set(Calendar.DAY_OF_MONTH, 1);

							int ultimo_dia = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
							fecha_pago = ultimo_dia + "/" + mes + "/" + agno;
						} else {
							fecha_pago = dia + "/" + mes + "/" + agno;
						}

						// **************************************************** */
					} else {
						fecha_pago = fecha_futuro(fecha_pago, prestamo.getForma_pago());
					}

//					System.out.println(cuota + " - " + capital + " - " + interes + " " + total_pagar);

					Amortizacion amortizacion = new Amortizacion();
					amortizacion.setFecha(fecha_pago);
					amortizacion.setCuota(formato2d(cuota));
					amortizacion.setCapital(formato2d(capital));
					amortizacion.setInteres(formato2d(interes));
					amortizacion.setSaldo(formato2d(total_pagar));

					detalles.add(amortizacion);
					x++;
				}

			} else if (prestamo.getTipo().equals("3")) {
				// Cuotas variables
				double interes = prestamo.getTasa().doubleValue();
				double cuota = prestamo.getMonto()
						* (Math.pow((1 + (interes / 100)), prestamo.getPagos()) * (interes / 100))
						/ (Math.pow((1 + (interes / 100)), prestamo.getPagos()) - 1);
				
				double capital = 0;
				double total_pagar = 0;

				for (int i = 1; i <= prestamo.getPagos(); i++) {

					if (prestamo.getForma_pago().equals("30")) {

						mes++;

						if (mes == 13) {
							mes = 1;
							agno++;
						}

						int dia = fecha_pagoTemp.getDayOfMonth();

						if (dia == 31 || (dia > 28 && mes == 2)) {

							Calendar calendar = Calendar.getInstance();
							calendar.set(Calendar.MONTH, mes);
							calendar.set(Calendar.YEAR, agno);
							calendar.set(Calendar.DAY_OF_MONTH, 1);

							int ultimo_dia = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
							fecha_pago = ultimo_dia + "/" + mes + "/" + agno;
						} else {
							fecha_pago = dia + "/" + mes + "/" + agno;
						}

					} else {
						fecha_pago = fecha_futuro(fecha_pago, prestamo.getForma_pago());
					}
					
					if(i>1) {
						double tempPorcent = prestamo.getTasa() / 100;
						tempPorcent = tempPorcent * capital;
						capital += tempPorcent;
						interes = cuota - capital;
						total_pagar = ((total_pagar - interes) - capital) + interes;

					}else {
						interes = prestamo.getMonto() * (interes / 100);
						capital = cuota - interes;
						total_pagar = prestamo.getMonto() - capital;
					}

					Amortizacion amortizacion = new Amortizacion();
					amortizacion.setFecha(fecha_pago);
					amortizacion.setCuota(formato2d(cuota));
					amortizacion.setCapital(formato2d(capital));
					amortizacion.setInteres(formato2d(interes));
					amortizacion.setSaldo(formato2d(total_pagar));

					detalles.add(amortizacion);

					total_cuota += cuota;
					total_capital += capital;
					total_interes += interes;
				}
			}
		}else {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");  
			Calendar calendar = Calendar.getInstance();
			Amortizacion amortizacion = new Amortizacion();
			amortizacion.setFecha(sdf.format(calendar.getTime()));
			amortizacion.setCuota(0.00);
			amortizacion.setCapital(0.00);
			amortizacion.setInteres(0.00);
			amortizacion.setSaldo(0.00);
			detalles.add(amortizacion);
		}

		model.addAttribute("detalles", detalles);
		model.addAttribute("totalCuota", total_cuota >0 ? formato2d(total_cuota):total_cuota);
		model.addAttribute("totalCapital", total_capital >0 ? formato2d(total_capital) : total_capital);
		model.addAttribute("totalInteres", total_interes >0 ? formato2d(total_interes) : total_interes);
		model.addAttribute("totalNeto", total_neto >0 ? formato2d(total_neto) : total_neto);
		return "prestamos/form :: #cuerpo_amortizacion";
	}
	
	public double formato2d(double number) {
		number = Math.round(number * 100);
		number = number/100;
		return number;
	}
	
	public String fecha_futuro(String fecha_pago, String forma_pago) {
		    String[] fechaTemp = null;
		    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");  

			if (Pattern.matches("/[0-9]{1,2}\\/[0-9]{1,2}\\/([0-9][0-9]){1,2}/", fecha_pago)){
				fechaTemp = fecha_pago.split("/");
		    }
		    
		    if (Pattern.matches("/[0-9]{1,2}-[0-9]{1,2}-([0-9][0-9]){1,2}/",fecha_pago)){
		    	fechaTemp = fecha_pago.split("-");
		    }
		    
		    if(fechaTemp==null) {
		    	if(fecha_pago.contains("-")) {
		    		String[] temporal = fecha_pago.split("-");
		    		if(temporal.length == 3) {
		    			String dia = temporal[0];
		    			if(!dia.contains("0")) {
			    			dia = "0"+dia;
		    			}
		    			String mes = temporal[1];
		    			if(!mes.contains("0")) {
		    				mes = "0"+mes;
		    			}
		    			String temp = dia + "-" + mes + "-" +temporal[2];
		    			fechaTemp = temp.split("-");
		    		}

		    	}else if(fecha_pago.contains("/")) {

		    		String[] temporal = fecha_pago.split("/");
		    		if(temporal.length == 3) {
		    			String dia = temporal[0];
		    			if(!dia.contains("0")) {
			    			dia = "0"+dia;
		    			}
		    			String mes = temporal[1];
		    			if(!mes.contains("0")) {
		    				mes = "0"+mes;
		    			}
		    			String temp = dia + "/" + mes + "/" +temporal[2];
		    			fechaTemp = temp.split("/");
		    		}

		    	
		    	}
		    }
		    
		    Calendar calendar = Calendar.getInstance();
		    calendar.set(Calendar.MONTH, Integer.parseInt(fechaTemp[1]));
		    calendar.set(Calendar.YEAR, Integer.parseInt(fechaTemp[2]));
		    calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(fechaTemp[0])+Integer.parseInt(forma_pago));
		    
		    String nuevaFecha = sdf.format(calendar.getTime());
		return nuevaFecha;
	}

	@InitBinder
	public void initBinder(WebDataBinder webDataBinder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}
}
