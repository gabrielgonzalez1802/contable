package com.contable.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.contable.model.Amortizacion;
import com.contable.model.Carpeta;
import com.contable.model.Cliente;
import com.contable.model.Cuenta;
import com.contable.model.Prestamo;
import com.contable.model.PrestamoAdicional;
import com.contable.model.PrestamoDetalle;
import com.contable.model.PrestamoInteresDetalle;
import com.contable.model.Usuario;
import com.contable.service.ICarpetasService;
import com.contable.service.IClientesService;
import com.contable.service.ICuentasService;
import com.contable.service.IPrestamosAdicionalesService;
import com.contable.service.IPrestamosDetallesService;
import com.contable.service.IPrestamosInteresesDetallesService;
import com.contable.service.IPrestamosService;

@Controller
@RequestMapping("/prestamos")
public class PrestamosController {
	
	@Autowired
	private IPrestamosService servicePrestamos;
	
	@Autowired
	private IPrestamosDetallesService servicePrestamosDetalles;
	
	@Autowired
	private IClientesService serviceClientes;
	
	@Autowired
	private ICuentasService serviceCuentas;
	
	@Autowired
	private ICarpetasService serviceCarpetas;
	
	@Autowired
	private IPrestamosAdicionalesService servicePrestamosAdicionales;
	
	@Autowired
	private IPrestamosInteresesDetallesService servicePrestamosInteresesDetalles;
	
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	
	@GetMapping("/")
	public String listaPrestamos(Model model) {
		return "prestamos/listaPrestamos :: listaPrestamo";
	}
	
	@GetMapping("/agregar")
	public String agregarPrestamos(Model model, HttpSession session) {
		Prestamo prestamo = new Prestamo();
		Cliente cliente = new Cliente();
		prestamo.setCuenta(new Cuenta());
		Carpeta carpeta = serviceCarpetas.buscarPorId((Integer) session.getAttribute("carpeta"));
		List<Cuenta> cuentas = serviceCuentas.buscarPorCarpeta(carpeta);
		if(!cuentas.isEmpty()) {
			for (Cuenta cuenta : cuentas) {
				cuenta.setBanco(cuenta.getBanco()+" - "+formato2d(cuenta.getMonto()));
			}
		}
		if((Integer)session.getAttribute("cliente")==0) {
			model.addAttribute("prestamo",  new Prestamo());
			model.addAttribute("cliente", cliente);
			model.addAttribute("carpeta", carpeta);
			model.addAttribute("cuentas", cuentas);
			model.addAttribute("msg", "NOCLIENTE");
			model.addAttribute("tipoDocumentoAcctPrestamo", "cedula");
			return "prestamos/form :: form";
		}else {
			cliente = serviceClientes.buscarPorId((Integer) session.getAttribute("cliente"));
		}
		model.addAttribute("cliente", cliente);
		model.addAttribute("carpeta", carpeta);
		model.addAttribute("cuentas", cuentas);
		model.addAttribute("tipoDocumentoAcctPrestamo", cliente.getTipoDocumento());
		model.addAttribute("prestamo",  new Prestamo());
		return "prestamos/form :: form";
	}
	
	@GetMapping("/actualizarCarpeta")
	public String actualizarCarpeta(Model model, HttpSession session) {
		Carpeta carpeta = serviceCarpetas.buscarPorId((Integer) session.getAttribute("carpeta"));
		Cliente cliente = serviceClientes.buscarPorId((Integer) session.getAttribute("cliente"));
		session.setAttribute("carpeta", carpeta.getId());
		model.addAttribute("carpeta", carpeta);
		model.addAttribute("cliente", cliente);
		model.addAttribute("tipoDocumentoAcctPrestamo", cliente.getTipoDocumento());
		return "prestamos/form :: #buscadorAgregarPrestamo";
	}
	
	@GetMapping("/actualizarCarpetaPrincial")
	public String actualizarCarpetaPrincial(Model model, HttpSession session) {
		Carpeta carpeta = serviceCarpetas.buscarTipoCarpeta(1).get(0);
		Cliente cliente = serviceClientes.buscarPorId((Integer) session.getAttribute("cliente"));
		session.setAttribute("carpeta", carpeta.getId());
		model.addAttribute("carpeta", carpeta);
		model.addAttribute("cliente", cliente);
		model.addAttribute("tipoDocumentoAcctPrestamo", cliente.getTipoDocumento());
		return "prestamos/form :: #buscadorAgregarPrestamo";
	}
	
	@GetMapping("/actualizarCuentas/{id}")
	public String actualizarCuentas(Model model, @PathVariable(name = "id") Integer idCarpeta) {
		Carpeta carpeta = serviceCarpetas.buscarPorId(idCarpeta);
		List<Cuenta> cuentas = serviceCuentas.buscarPorCarpeta(carpeta);
		model.addAttribute("cuentas", cuentas);
		return "prestamos/form :: #id_cuenta";
	}
	
	@PostMapping("/getInfoCliente")
	public String getInfoCliente(Model model, Integer carpeta, String tipoDocumento, String item, HttpSession session) {
		Cliente cliente = new Cliente();
//		Integer idCarpeta = (Integer) session.getAttribute("carpeta");
		Carpeta carpetaTemp = serviceCarpetas.buscarPorId(carpeta);
		session.setAttribute("carpeta", carpetaTemp.getId());
		model.addAttribute("carpeta", carpetaTemp);
		if (tipoDocumento.equals("cedula")) {
			cliente = serviceClientes.buscarPorCedula(item);
			if(cliente != null) {
				session.setAttribute("cliente", cliente.getId());
			}
		}else if(tipoDocumento.equals("otro")){
			cliente = serviceClientes.buscarPorOtro(item);
			if(cliente != null) {
				session.setAttribute("cliente", cliente.getId());
			}
		}else {
			//busqueda por nombre
			List<Cliente> clientes = serviceClientes.buscarPorNombre(item).stream().
					filter(c -> c.getEstado() == 1).collect(Collectors.toList());
			if(clientes.isEmpty()) {
				cliente = null;
			}else {
				if(clientes.size()>1) {
					model.addAttribute("clientes", clientes);
					return "clientes/infoCliente :: infoClienteLista";
				}else {
					cliente = clientes.get(0);
					if(cliente != null) {
						session.setAttribute("cliente", cliente.getId());
					}
				}
			}
		}
		
		if(cliente == null) {
			session.setAttribute("cliente", 0);
			model.addAttribute("cliente",new Cliente());
			model.addAttribute("msg", "No se encontro el cliente");
			model.addAttribute("carpeta",carpetaTemp);
			return "prestamos/form :: #buscadorAgregarPrestamo"; 
		}

		model.addAttribute("carpeta",carpetaTemp);
		model.addAttribute("cliente", cliente);
		model.addAttribute("tipoDocumentoAcctPrestamo", cliente.getTipoDocumento());
		return "prestamos/form :: #buscadorAgregarPrestamo";
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
			interes = (prestamo.getMonto()*(prestamo.getTasa()/100)*(prestamo.getPagos().doubleValue()/fre))/prestamo.getPagos();

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
		
		Double tasa = ((100) * interes * prestamo.getPagos()) / (prestamo.getMonto() *(prestamo.getPagos().doubleValue()/fre)) ;
		
		cuota = capital + interes;

		
		model.addAttribute("capital", formato2d(capital));
		model.addAttribute("tasa", formato2d(tasa));
		model.addAttribute("interes", formato2d(interes));
		model.addAttribute("total_pagar", formato2d(total_pagar));
		return "prestamos/form :: #calculosTasa";
	}
	
	@PostMapping("/amortizar")
	public String amortizar(Model model, @ModelAttribute("prestamo") Prestamo prestamo) throws ParseException {
		int fre = 0;
		double total_cuota = 0;
		double total_capital = 0;
		double total_interes = 0;
		double total_neto = 0;
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		
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
			
			Date date = formato.parse(prestamo.getFechaTemp());
			LocalDate fecha_pagoTemp = date.toInstant().atZone(ZoneId.systemDefault().systemDefault())
					.toLocalDate();
			int mes = fecha_pagoTemp.getMonthValue();
			int agno = fecha_pagoTemp.getYear();
			String fecha_pago = fecha_pagoTemp.getDayOfMonth() + "-" + mes + "-" + agno;

			if (prestamo.getTipo().equals("1")) {
				//Coutas Fijas
				int x = 0;

				double interes = (prestamo.getMonto() * (prestamo.getTasa() / 100) * (prestamo.getPagos().doubleValue() / fre))
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
					x++;
					Amortizacion amortizacion = new Amortizacion();
					amortizacion.setFecha(fecha_pago);
					amortizacion.setCuota(formato2d(cuota));
					amortizacion.setCapital(formato2d(capital));
					amortizacion.setInteres(formato2d(interes));
					amortizacion.setSaldo(formato2d(total_pagar));
					amortizacion.setNumero(x);
					detalles.add(amortizacion);
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
		return "index :: #cuerpo_amortizacion";
	}
	
	@PostMapping("/guardar")
	@ResponseBody
	public ResponseEntity<String> guardar(HttpSession session, Model model, @ModelAttribute("prestamo") Prestamo prestamo) throws ParseException {
		int fre = 0;
		double total_cuota = 0;
		double total_capital = 0;
		double total_interes = 0;
		double total_neto = 0;
		Integer response = 0;
		
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formato2 = new SimpleDateFormat("dd/MM/yyyy");
				
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
			
			Date date = formato.parse(prestamo.getFechaTemp());
			ZoneId zoneId = ZoneId.systemDefault();
			LocalDate fecha_pagoTemp = date.toInstant().atZone(zoneId)
					.toLocalDate();
			int mes = fecha_pagoTemp.getMonthValue();
			int agno = fecha_pagoTemp.getYear();
			String fecha_pago = fecha_pagoTemp.getDayOfMonth() + "-" + mes + "-" + agno;

			if (prestamo.getTipo().equals("1")) {
				//Coutas Fijas
				int x = 0;

				double interes = (prestamo.getMonto() * (prestamo.getTasa() / 100) * (prestamo.getPagos().doubleValue() / fre))
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
					x++;
					Amortizacion amortizacion = new Amortizacion();
					amortizacion.setFecha(fecha_pago);
					amortizacion.setCuota(formato2d(cuota));
					amortizacion.setCapital(formato2d(capital));
					amortizacion.setInteres(formato2d(interes));
					amortizacion.setSaldo(formato2d(total_pagar));
					amortizacion.setNumero(x);
					detalles.add(amortizacion);
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
			if(prestamo.getTipo().equals("2")) {
				//interes
				total_neto = prestamo.getMonto();
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
		}
		
		//Guardamos el prestamo
		Cliente cliente = serviceClientes.buscarPorId(prestamo.getIdClienteTemp());
		Carpeta carpeta = serviceCarpetas.buscarPorId(prestamo.getIdCarpetaTemp());
		
		int codigo = 1;
		
		//Buscamos la lista de prestamos asociados a la carpeta
		List<Prestamo> prestamos = servicePrestamos.buscarPorCarpeta(carpeta);
		if(!prestamos.isEmpty()) {
			Prestamo tempPrestamo = prestamos.get(prestamos.size()-1);
			codigo = tempPrestamo.getCodigo()+1;
		}
		
		if(prestamo.getIdCuentaTemp()!=null) {
			Cuenta cuenta = serviceCuentas.buscarPorId(prestamo.getIdCuentaTemp());
			prestamo.setCuenta(cuenta);
			if((cuenta.getMonto()-prestamo.getMonto()) < 0) {
				response = 0;
//				return new ResponseEntity<>(response.toString(), HttpStatus.OK);
			}
		}else {
			prestamo.setCuenta(null);
		}
		
		double capital = total_capital >0 ? formato2d(total_capital) : total_capital;
				
		prestamo.setCliente(cliente);
		prestamo.setCarpeta(carpeta);
		prestamo.setUsuario(usuario);
		prestamo.setBalance(total_neto >0 ? formato2d(total_neto) : total_neto);
//		prestamo.setEstado(estado); //verificar
		prestamo.setCodigo(codigo);
		prestamo.setFecha(formato.parse(prestamo.getFechaTemp()));
		prestamo.setTotal_cuota(total_cuota >0 ? formato2d(total_cuota):total_cuota);
		prestamo.setTotal_capital(!prestamo.getTipo().equals("2")?capital:prestamo.getMonto());
		prestamo.setTotal_interes(total_interes >0 ? formato2d(total_interes) : total_interes);
		prestamo.setTotal_neto(total_neto >0 ? formato2d(total_neto) : total_neto);
		servicePrestamos.guardar(prestamo);
		
		//Guardamos los detalles de la amortizacion en el prestamo
		if(!prestamo.getTipo().equals("2")) {
			for (Amortizacion amortizacion : detalles) {
				PrestamoDetalle prestamoDetalle = new PrestamoDetalle();
				prestamoDetalle.setPrestamo(prestamo);
				prestamoDetalle.setBalance(amortizacion.getSaldo());
				prestamoDetalle.setCapital(amortizacion.getCapital());
				prestamoDetalle.setNumero(amortizacion.getNumero());
				prestamoDetalle.setCuota(amortizacion.getCuota());
				prestamoDetalle.setFecha(new Date());
				prestamoDetalle.setFechaGenerada(formato2.parse(amortizacion.getFecha()));
				prestamoDetalle.setEstado_cuota("Normal");
//				prestamoDetalle.setFechaInteres(new Date());
				prestamoDetalle.setGenerarInteres(amortizacion.getInteres()>0?1:0);
				prestamoDetalle.setInteres(amortizacion.getInteres());
				prestamoDetalle.setInteres_mora(prestamo.getMora());
//				prestamoDetalle.setMonto(amortizacion.getCuota());
//				prestamoDetalle.setMora(amortizacion.getm);
				prestamoDetalle.setPago(0);
				servicePrestamosDetalles.guardar(prestamoDetalle);
			}
			
			if(prestamo.getCantidad_pagos() > 0) {
				double adicionales = prestamo.getGastos_cierre() / prestamo.getCantidad_pagos();
				List<PrestamoDetalle> prestamosDetalles = servicePrestamosDetalles.buscarPorPrestamo(prestamo);
				
				for (int i = 0; i < prestamo.getCantidad_pagos(); i++) {
					PrestamoAdicional prestamoAdicional = new PrestamoAdicional();
					prestamoAdicional.setMonto(adicionales);
					prestamoAdicional.setFecha(new Date());
					prestamoAdicional.setMotivo("Gastos Cierre");
					prestamoAdicional.setPrestamo(prestamo);
					prestamoAdicional.setPrestamoDetalle(prestamosDetalles.get(i));
					prestamoAdicional.setUsuario(usuario);
					servicePrestamosAdicionales.guardar(prestamoAdicional);
				}
			}
		}else {
			//Interes
			if(prestamo.getCantidad_pagos() > 0) {
				double adicionales = prestamo.getGastos_cierre() / prestamo.getCantidad_pagos();
				for (int i = 0; i < prestamo.getCantidad_pagos(); i++) {
					LocalDateTime fechaAcct =  LocalDateTime.now();
					LocalDateTime fechaVenciminto = fechaAcct.plusMonths(1);
					Date vencimiento = convertToDateViaInstant(fechaVenciminto);
					PrestamoAdicional prestamoAdicional = new PrestamoAdicional();
					prestamoAdicional.setMonto(adicionales);
					prestamoAdicional.setFecha(prestamo.getFecha());
					prestamoAdicional.setMotivo("Gastos Cierre");
					prestamoAdicional.setPrestamo(prestamo);
					prestamoAdicional.setUsuario(usuario);
					prestamoAdicional.setFecha_vencimiento(vencimiento);
					servicePrestamosAdicionales.guardar(prestamoAdicional);
				}
			}
		}
		
		if(prestamo.getId()!=null) {
			if(prestamo.getCuenta()!=null) {
				Cuenta cuenta = prestamo.getCuenta();
				cuenta.setMonto(cuenta.getMonto()-prestamo.getMonto());
				serviceCuentas.guardar(cuenta);
			}
			response = 1;
		}
		
		return new ResponseEntity<>(response.toString(), HttpStatus.OK);
	}
	
	@GetMapping("/totalesGenerales/{id}")
	public String totalesGeneralesPrestamo(Model model, @PathVariable(name = "id") Integer id) {
		Prestamo prestamo = servicePrestamos.buscarPorId(id);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");  
		double totalCargo = 0;
		double totalCuota = 0;
		double totalBalance = 0;
		double totalMora = 0;
		if(prestamo.getTipo().equals("2")) {
			//interes
			List<Amortizacion> detalles = new LinkedList<>();
			List<PrestamoInteresDetalle> prestamoInteresDetalles = servicePrestamosInteresesDetalles.buscarPorPrestamo(prestamo);
			List<PrestamoAdicional> cargosAdicionales = servicePrestamosAdicionales.buscarPorPrestamo(prestamo);
			int count = 0;
			double mora = 0;
			double interes = 0;
			if(!prestamoInteresDetalles.isEmpty()) {
				for (PrestamoInteresDetalle prestamoInteresDetalle : prestamoInteresDetalles) {
					count++;
					String estado = "Normal";
					if(prestamoInteresDetalle.getEstado()==2) {
						estado = "Atraso";
					}
					Amortizacion amortizacion = new Amortizacion();
					amortizacion.setNumero(count);
					amortizacion.setFecha(sdf.format(prestamoInteresDetalle.getFecha_cuota()));
//					amortizacion.setCuota();
					amortizacion.setCapital(prestamoInteresDetalle.getCapital());
					amortizacion.setInteres(prestamoInteresDetalle.getInteres());
//					amortizacion.setSaldo(prestamoDetalle.getBalance());
//					amortizacion.setNumero(prestamoDetalle.getNumero());
					amortizacion.setMora(prestamoInteresDetalle.getMora());
					amortizacion.setEstado(estado);
					amortizacion.setAtraso(prestamoInteresDetalle.getDias_atraso());
					detalles.add(amortizacion);
					mora+=prestamoInteresDetalle.getMora();
					interes+=prestamoInteresDetalle.getInteres();
					
//					if(prestamoInteresDetalle.getDias_atraso()>0) {
//					}
					totalCuota+=prestamoInteresDetalle.getInteres();
					totalCuota-=prestamoInteresDetalle.getInteres_pagado();
					
					totalMora+=prestamoInteresDetalle.getMora();
					totalMora-=prestamoInteresDetalle.getMora_pagada();
				}
				if(!cargosAdicionales.isEmpty()) {
					for (PrestamoAdicional prestamoAdicional : cargosAdicionales) {
						totalCargo+=prestamoAdicional.getMonto();
						totalCargo-=prestamoAdicional.getMonto_pagado();
					}
				}
				detalles.get(0).setCuota(formato2d(mora+interes));
			}
		}else {
			//cuotas vencidas
			List<PrestamoDetalle> prestamoDetalles = servicePrestamosDetalles.buscarPorPrestamoEstado(prestamo, 2);
			if(!prestamoDetalles.isEmpty()) {
				for (PrestamoDetalle prestamoDetalle : prestamoDetalles) {
//					totalCuota += prestamoDetalle.getBalance();
					totalCuota += prestamoDetalle.getInteres()+prestamoDetalle.getCapital();
					//Verificamos los cargos
					List<PrestamoAdicional> cargosAdicionales = servicePrestamosAdicionales.buscarPorPrestamoDetalle(prestamoDetalle);
					if(!cargosAdicionales.isEmpty()) {
						for (PrestamoAdicional cargoAdicional : cargosAdicionales) {
							totalCargo+=cargoAdicional.getMonto();
						}
					}
					totalMora+=prestamoDetalle.getMora();
				}
			}
		}
		//Calculo del balance: suma de balances vencidos + cargos + las moras.
		totalBalance = totalCuota+totalCargo+totalMora;
		model.addAttribute("totalBalance", formato2d(totalBalance));
		model.addAttribute("totalCuota", formato2d(totalCuota));
		model.addAttribute("totalCargo", formato2d(totalCargo));
		model.addAttribute("totalMora", formato2d(totalMora));
		return "clientes/infoCliente :: #totalesGeneralesPrestamo";
	}
	
	@GetMapping("/detalleAmortizacion/{id}")
	public String detalleAmortizacion(Model model, @PathVariable(name = "id") Integer id) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");  
		Prestamo prestamo = servicePrestamos.buscarPorId(id);
		List<Amortizacion> detalles = new LinkedList<>();
		List<PrestamoInteresDetalle> prestamoInteresDetalles = servicePrestamosInteresesDetalles.buscarPorPrestamo(prestamo);
		
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
					}
					Amortizacion amortizacion = new Amortizacion();
					amortizacion.setNumero(count);
					amortizacion.setFecha(sdf.format(prestamoInteresDetalle.getFecha_cuota()));
//					amortizacion.setCuota();
					capital = prestamoInteresDetalle.getCapital();
					capital-= prestamoInteresDetalle.getCapital_pagado();
//					amortizacion.setCapital(prestamoInteresDetalle.getCapital());
					amortizacion.setInteres(prestamoInteresDetalle.getInteres()-prestamoInteresDetalle.getInteres_pagado());
//					amortizacion.setSaldo(prestamoDetalle.getBalance());
//					amortizacion.setNumero(prestamoDetalle.getNumero());
					amortizacion.setMora(prestamoInteresDetalle.getMora()-prestamoInteresDetalle.getMora_pagada());
					amortizacion.setEstado(estado);
					amortizacion.setAtraso(prestamoInteresDetalle.getDias_atraso());
					detalles.add(amortizacion);
					mora+=prestamoInteresDetalle.getMora();
					interes+=prestamoInteresDetalle.getInteres();
				}
				detalles.get(0).setCuota(formato2d(mora+interes));
				detalles.get(0).setCapital(formato2d(capital));
			}
		}else {
			//Cuotas
			List<PrestamoDetalle> prestamoDetalles = servicePrestamosDetalles.buscarPorPrestamo(prestamo);
			detalles = new LinkedList<>();
			for (PrestamoDetalle prestamoDetalle : prestamoDetalles) {
				Amortizacion amortizacion = new Amortizacion();
				amortizacion.setFecha(sdf.format(prestamoDetalle.getFechaGenerada()));
				amortizacion.setCuota(prestamoDetalle.getCuota());
				amortizacion.setCapital(prestamoDetalle.getCapital()-prestamoDetalle.getCapital_pagado());
				amortizacion.setInteres(prestamoDetalle.getInteres()-prestamoDetalle.getInteres_pagado());
				amortizacion.setSaldo(prestamoDetalle.getBalance());
				amortizacion.setNumero(prestamoDetalle.getNumero());
				amortizacion.setMora(prestamoDetalle.getMora()-prestamoDetalle.getMora_pagada());
				amortizacion.setEstado(prestamoDetalle.getEstado_cuota());
				amortizacion.setAtraso(prestamoDetalle.getDias_atraso());
				detalles.add(amortizacion);
			}
		}
		
		model.addAttribute("detalles", detalles);
		model.addAttribute("totalCuota", prestamo.getTotal_cuota());
		model.addAttribute("totalCapital", prestamo.getTotal_capital());
		model.addAttribute("totalInteres", prestamo.getTotal_interes());
		model.addAttribute("totalNeto", prestamo.getTotal_neto());
		return "index :: #cuerpo_amortizacion";
	}
	
	public double formato2d(double number) {
		number = Math.round(number * 100);
		number = number/100;
		return number;
	}
	
	public String fecha_futuro(String fecha_pago, String forma_pago) throws ParseException {
		    String[] fechaTemp = null;
		    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");  

			if (fecha_pago.contains("-")) {
				String[] temporal = fecha_pago.split("-");
				if (temporal.length == 3) {
					String dia = temporal[0];
					if (!dia.contains("0")) {
						dia = "0" + dia;
					}
					String mes = temporal[1];
					if (!mes.contains("0")) {
						mes = "0" + mes;
					}
					String temp = dia + "-" + mes + "-" + temporal[2];
					fechaTemp = temp.split("-");
				}

			} else if (fecha_pago.contains("/")) {

				String[] temporal = fecha_pago.split("/");
				if (temporal.length == 3) {
					String dia = temporal[0];
					if (!dia.contains("0")) {
						dia = "0" + dia;
					}
					String mes = temporal[1];
					if (!mes.contains("0")) {
						mes = "0" + mes;
					}
					String temp = dia + "/" + mes + "/" + temporal[2];
					fechaTemp = temp.split("/");
				}

			}
			
		Date date =	sdf.parse(fechaTemp[0]+"/"+fechaTemp[1]+"/"+fechaTemp[2]);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_YEAR, Integer.parseInt(forma_pago));
		Date newDate = calendar.getTime();
		String fecha = sdf.format(newDate);
		return fecha;
	}
	
	@GetMapping("/cuotasNoPagadas/{id}")
	public String cargarCuotasNoPagadas(Model model, @PathVariable("id") Integer id) {
		Prestamo prestamo = servicePrestamos.buscarPorId(id);
		List<PrestamoDetalle> prestamoDetalles = servicePrestamosDetalles.buscarPorPrestamo(prestamo).stream().filter(p -> p.getPago() == 0).collect(Collectors.toList());
		if(!prestamoDetalles.isEmpty()) {
//			for (PrestamoDetalle prestamoDetalle : prestamoDetalles) {
//				prestamoDetalle.
//			}
		}
		model.addAttribute("prestamoDetalles", prestamoDetalles);
		return "clientes/infoCliente :: #selectCuotaCargo";
	}
	
	@PostMapping("/guardarCargoCuota/")
	@ResponseBody
	public ResponseEntity<String> guardarCargoCuota(Model model, HttpSession session,
			Integer idPrestamo, String motivo, 
			Double monto,  Integer cuota) {
		Prestamo prestamo = servicePrestamos.buscarPorId(idPrestamo);
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		PrestamoAdicional prestamoAdicional = new PrestamoAdicional();
		PrestamoDetalle prestamoDetalle = null;
		Integer response = 0;
		if(cuota>0) {
			prestamoDetalle = servicePrestamosDetalles.buscarPorId(cuota);
			prestamoAdicional.setPrestamoDetalle(prestamoDetalle);
		}
//		LocalDateTime fechaAcct =  LocalDateTime.now();
//		LocalDateTime fechaVenciminto = fechaAcct.plusMonths(1);
//		Date vencimiento = convertToDateViaInstant(fechaVenciminto);
		prestamoAdicional.setFecha(new Date());
		
		if(prestamo.getTipo().equals("2")) {
			//Interes
			prestamoAdicional.setFecha_vencimiento(new Date());
		}else {
			//cuotas
			prestamoAdicional.setFecha_vencimiento(prestamoDetalle.getFechaGenerada());
		}
		
		prestamoAdicional.setMonto(monto);
		prestamoAdicional.setMotivo(motivo);
		prestamoAdicional.setPrestamo(prestamo);
		prestamoAdicional.setUsuario(usuario);
		servicePrestamosAdicionales.guardar(prestamoAdicional);
		if(prestamoAdicional.getId()!=null) {
			response = 1;
		}
		return new ResponseEntity<>(response.toString(), HttpStatus.OK);
	}
	
	@PostMapping("/infoAbonos/")
	@ResponseBody
	public ResponseEntity<String> infoAbonos(Model model, HttpSession session, Integer idPrestamo) {
		Prestamo prestamo = servicePrestamos.buscarPorId(idPrestamo);
		if(prestamo.getTipo().equals("2")) {
			//Interes
			double moraPagada = 0;
			double interesPagado = 0;
			double capitalPagado = 0;
			double cargosPagado = 0;
			List<PrestamoInteresDetalle> prestamoInteresDetalles = servicePrestamosInteresesDetalles.buscarPorPrestamo(prestamo);
			List<PrestamoAdicional> prestamoAdicionales = servicePrestamosAdicionales.buscarPorPrestamo(prestamo);
			if(!prestamoInteresDetalles.isEmpty()) {
				for (PrestamoInteresDetalle prestamoInteresDetalle : prestamoInteresDetalles) {
					moraPagada+=prestamoInteresDetalle.getMora_pagada();
					interesPagado+=prestamoInteresDetalle.getInteres_pagado();
					capitalPagado+=prestamoInteresDetalle.getCapital_pagado();
				}
			}
			if(!prestamoAdicionales.isEmpty()) {
				for (PrestamoAdicional prestamoAdicional : prestamoAdicionales) {
					cargosPagado+=prestamoAdicional.getMonto();
				}
			}
		}else {
			//cuotas
		}
		return new ResponseEntity<>("1", HttpStatus.OK);
	}
	
	@PostMapping("/guardarAbonoCuota/")
	@ResponseBody
	public ResponseEntity<String> guardarAbonoCuota(Model model, HttpSession session,
			Integer idPrestamo, Double monto, Integer tipoCuota) {
		Prestamo prestamo = servicePrestamos.buscarPorId(idPrestamo);
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Amortizacion> detalles = new LinkedList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");  
		double disponible = monto;
		double totalMora = 0;
		
		if(tipoCuota == 1) {
			if(prestamo.getTipo().equals("2")) {
				List<PrestamoInteresDetalle> prestamoInteresDetalles = servicePrestamosInteresesDetalles.buscarPorPrestamo(prestamo);

				//Pagamos los cargos
				List<PrestamoAdicional> prestamosAdicionales = servicePrestamosAdicionales.buscarPorPrestamoEstado(prestamo, 0);
				if(!prestamosAdicionales.isEmpty()) {
					for (PrestamoAdicional prestamoAdicional : prestamosAdicionales) {
						if(prestamoAdicional.getFecha_vencimiento()!=null && 
								prestamoAdicional.getFecha_vencimiento().before(new Date()) ||
								prestamoAdicional.getFecha_vencimiento().equals(new Date())) {
							if(disponible+prestamoAdicional.getMonto_pagado()>=prestamoAdicional.getMonto()) {
								prestamoAdicional.setMonto_pagado(prestamoAdicional.getMonto());
								prestamoAdicional.setEstado(1);
								disponible-=prestamoAdicional.getMonto_pagado();
							}else {
								prestamoAdicional.setMonto_pagado(prestamoAdicional.getMonto_pagado()+disponible);
								disponible=0;
							}
							servicePrestamosAdicionales.guardar(prestamoAdicional);
						}
					}
				}
				
				//Pagamos las moras
				if(disponible>0) {
					if(!prestamoInteresDetalles.isEmpty()) {
						for (PrestamoInteresDetalle prestamoInteresDetalle : prestamoInteresDetalles) {
							if(disponible>0) {
								if(prestamoInteresDetalle.getMora()>0) {
									if(prestamoInteresDetalle.getMora_pagada().doubleValue() < prestamoInteresDetalle.getMora().doubleValue()) {
										if(prestamoInteresDetalle.getMora_pagada().doubleValue() == prestamoInteresDetalle.getMora().doubleValue()) {
											prestamoInteresDetalle.setMora_pagada(disponible+prestamoInteresDetalle.getMora_pagada());
											disponible=0;
										}else {
											double moraTemp = prestamoInteresDetalle.getMora() - prestamoInteresDetalle.getMora_pagada();
											if(disponible<prestamoInteresDetalle.getMora()) {
												prestamoInteresDetalle.setMora_pagada(disponible);
											}else {
												prestamoInteresDetalle.setMora_pagada(prestamoInteresDetalle.getMora());
											}
											disponible-=moraTemp;
										}	
									}else {
										if(prestamoInteresDetalle.getMora_pagada().doubleValue() == prestamoInteresDetalle.getMora().doubleValue()) {
											continue;
										}else {
											prestamoInteresDetalle.setMora_pagada(prestamoInteresDetalle.getMora_pagada()+disponible);
											disponible=0;
										}
									}
									servicePrestamosInteresesDetalles.guardar(prestamoInteresDetalle);
								}
							}else {
								break;
							}
						}
					}
				}
				
				//Pagamos el interes (Busca todos los intereses)
				if(disponible>0) {
					if(!prestamoInteresDetalles.isEmpty()) {
						for (PrestamoInteresDetalle prestamoInteresDetalleTemp : prestamoInteresDetalles) {
							if(disponible>0) {
								if(prestamoInteresDetalleTemp.getInteres_pagado() < prestamoInteresDetalleTemp.getInteres()) {
									if(disponible+prestamoInteresDetalleTemp.getInteres_pagado()>=prestamoInteresDetalleTemp.getInteres()) {
										if(disponible+prestamoInteresDetalleTemp.getInteres_pagado().doubleValue()==prestamoInteresDetalleTemp.getInteres().doubleValue()) {
											prestamoInteresDetalleTemp.setInteres_pagado(disponible+prestamoInteresDetalleTemp.getInteres_pagado());
											disponible=0;
										}else {
											double interesTemp = prestamoInteresDetalleTemp.getInteres() - prestamoInteresDetalleTemp.getInteres_pagado();
											prestamoInteresDetalleTemp.setInteres_pagado( prestamoInteresDetalleTemp.getInteres());
											disponible-=interesTemp;
										}
									}else {
										if(prestamoInteresDetalleTemp.getInteres_pagado().doubleValue() == prestamoInteresDetalleTemp.getInteres().doubleValue()) {
											continue;
										}else {
											prestamoInteresDetalleTemp.setInteres_pagado(prestamoInteresDetalleTemp.getInteres_pagado()+disponible);
											disponible=0;
										}
									}
									servicePrestamosInteresesDetalles.guardar(prestamoInteresDetalleTemp);
								}
							}else {
								break;
							}
						}
					}
				}
				
				//Pagamos el capital
				if(disponible>0) {
					int count = 0;
					double capitalPagado = 0;
					for (PrestamoInteresDetalle prestamoInteresDetalle : prestamoInteresDetalles) {
						count++;
						if(prestamoInteresDetalle.getCapital_pagado()<prestamoInteresDetalle.getCapital()) {
							if(count == 1) {
								if(disponible+prestamoInteresDetalle.getCapital_pagado()>=prestamoInteresDetalle.getCapital()) {
									prestamoInteresDetalle.setCapital_pagado(prestamoInteresDetalle.getCapital());
									disponible-=prestamoInteresDetalle.getCapital_pagado();
								}else {
									prestamoInteresDetalle.setCapital_pagado(prestamoInteresDetalle.getCapital_pagado()+disponible);
									disponible=0;
								}
								capitalPagado = prestamoInteresDetalle.getCapital_pagado();
								prestamo.setBalance(prestamoInteresDetalle.getCapital()-capitalPagado);
								servicePrestamos.guardar(prestamo);
								servicePrestamosInteresesDetalles.guardar(prestamoInteresDetalle);
								continue;
							}
			
							prestamo.setBalance(prestamoInteresDetalle.getCapital()-capitalPagado);
							servicePrestamos.guardar(prestamo);
							prestamoInteresDetalle.setCapital_pagado(capitalPagado);
							servicePrestamosInteresesDetalles.guardar(prestamoInteresDetalle);
						}
					}
				}
				
				for (PrestamoInteresDetalle prestamoDetalles : prestamoInteresDetalles) {
					if(
//							prestamoDetalles.getCapital_pagado().equals(prestamoDetalles.getCapital()) &&
							prestamoDetalles.getInteres_pagado().equals(prestamoDetalles.getInteres()) && 
								prestamoDetalles.getMora_pagada().equals(prestamoDetalles.getMora())) {
						prestamoDetalles.setEstadoPago(1);
						prestamoDetalles.setEstado(1); //ggonzalez
						servicePrestamosInteresesDetalles.guardar(prestamoDetalles);
					}
				}
				
			}else {
				//Cuotas
				List<PrestamoDetalle> prestamoDetalles = servicePrestamosDetalles.buscarPorPrestamo(prestamo);

				//Pagamos los cargos
				List<PrestamoAdicional> prestamosAdicionales = servicePrestamosAdicionales.buscarPorPrestamoEstado(prestamo, 0);
				if(!prestamosAdicionales.isEmpty()) {
					for (PrestamoAdicional prestamoAdicional : prestamosAdicionales) {
						if (disponible + prestamoAdicional.getMonto_pagado() >= prestamoAdicional.getMonto()) {
							prestamoAdicional.setMonto_pagado(prestamoAdicional.getMonto());
							prestamoAdicional.setEstado(1);
							disponible -= prestamoAdicional.getMonto_pagado();
						} else {
							prestamoAdicional.setMonto_pagado(prestamoAdicional.getMonto_pagado() + disponible);
							disponible = 0;
						}
						servicePrestamosAdicionales.guardar(prestamoAdicional);
					}
				}
				
				//Pagamos las moras
				if(disponible>0) {
					if(!prestamoDetalles.isEmpty()) {
						for (PrestamoDetalle prestamoDetallesTemp : prestamoDetalles) {
							if(disponible>0) {
								if(prestamoDetallesTemp.getMora()>0) {
									if(prestamoDetallesTemp.getMora_pagada().doubleValue() < prestamoDetallesTemp.getMora().doubleValue()) {
										if(prestamoDetallesTemp.getMora_pagada().doubleValue() == prestamoDetallesTemp.getMora().doubleValue()) {
											prestamoDetallesTemp.setMora_pagada(disponible+prestamoDetallesTemp.getMora_pagada());
											disponible=0;
										}else {
											double moraTemp = prestamoDetallesTemp.getMora() - prestamoDetallesTemp.getMora_pagada();
											prestamoDetallesTemp.setMora_pagada(prestamoDetallesTemp.getMora());
											disponible-=moraTemp;
										}	
									}else {
										if(prestamoDetallesTemp.getMora_pagada().doubleValue() == prestamoDetallesTemp.getMora().doubleValue()) {
											continue;
										}else {
											prestamoDetallesTemp.setMora_pagada(prestamoDetallesTemp.getMora_pagada()+disponible);
											disponible=0;
										}
									}
									servicePrestamosDetalles.guardar(prestamoDetallesTemp);
								}
							}else {
								break;
							}
						}
					}
				}
								
				if (disponible > 0) {
					for (PrestamoDetalle prestamoDetalleTemp : prestamoDetalles) {
						if (disponible > 0) {
							// Pagamos el interes (Busca el interes de la cuota)
							if (prestamoDetalleTemp.getInteres_pagado() < prestamoDetalleTemp.getInteres()) {
								if (disponible + prestamoDetalleTemp.getInteres_pagado() >= prestamoDetalleTemp
										.getInteres()) {
									if (disponible + prestamoDetalleTemp.getInteres_pagado()
											.doubleValue() == prestamoDetalleTemp.getInteres().doubleValue()) {
										prestamoDetalleTemp.setInteres_pagado(
												disponible + prestamoDetalleTemp.getInteres_pagado());
										disponible = 0;
									} else {
										double interesTemp = prestamoDetalleTemp.getInteres()
												- prestamoDetalleTemp.getInteres_pagado();
										prestamoDetalleTemp.setInteres_pagado(prestamoDetalleTemp.getInteres());
										disponible -= interesTemp;
									}
								} else {
									if (prestamoDetalleTemp.getInteres_pagado().doubleValue() == prestamoDetalleTemp
											.getInteres().doubleValue()) {
										continue;
									} else {
										prestamoDetalleTemp.setInteres_pagado(
												prestamoDetalleTemp.getInteres_pagado() + disponible);
										disponible = 0;
									}
								}
								servicePrestamosDetalles.guardar(prestamoDetalleTemp);
							}
							
							//Pagamos el capital de la cuota
							if (prestamoDetalleTemp.getCapital_pagado() < prestamoDetalleTemp.getCapital()) {
								if (disponible + prestamoDetalleTemp.getCapital_pagado() >= prestamoDetalleTemp
										.getCapital()) {
									if (disponible + prestamoDetalleTemp.getCapital_pagado()
											.doubleValue() == prestamoDetalleTemp.getCapital().doubleValue()) {
										prestamoDetalleTemp.setCapital_pagado(
												disponible + prestamoDetalleTemp.getCapital_pagado());
										disponible = 0;
									} else {
										double capitalTemp = prestamoDetalleTemp.getCapital()
												- prestamoDetalleTemp.getCapital_pagado();
										prestamoDetalleTemp.setCapital_pagado(prestamoDetalleTemp.getCapital());
										disponible -= capitalTemp;
									}
								} else {
									if (prestamoDetalleTemp.getCapital_pagado().doubleValue() == prestamoDetalleTemp
											.getCapital().doubleValue()) {
										continue;
									} else {
										prestamoDetalleTemp.setCapital_pagado(
												prestamoDetalleTemp.getCapital_pagado() + disponible);
										disponible = 0;
									}
								}
								servicePrestamosDetalles.guardar(prestamoDetalleTemp);
							}

						} else {
							break;
						}
					}
				}
				
				for (PrestamoDetalle prestamoDetalle : prestamoDetalles) {
					if(
						prestamoDetalle.getCapital_pagado().equals(prestamoDetalle.getCapital()) &&
						prestamoDetalle.getInteres_pagado().equals(prestamoDetalle.getInteres()) && 
						prestamoDetalle.getMora_pagada().equals(prestamoDetalle.getMora())) {
						prestamoDetalle.setEstado(1);
						prestamoDetalle.setEstado_cuota("Saldo");
						prestamoDetalle.setDias_atraso(0);
						servicePrestamosDetalles.guardar(prestamoDetalle);
					}
				}
			}
		}
		
		return new ResponseEntity<>("1", HttpStatus.OK);
	}

	public Date convertToDateViaInstant(LocalDateTime dateToConvert) {
	    return java.util.Date
	      .from(dateToConvert.atZone(ZoneId.systemDefault())
	      .toInstant());
	}

	@InitBinder
	public void initBinder(WebDataBinder webDataBinder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}
}
