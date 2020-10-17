package com.contable.cron;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.contable.model.Prestamo;
import com.contable.model.PrestamoDetalle;
import com.contable.model.PrestamoInteresDetalle;
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
	
	private final Integer NORMAL = 0;
	private final Integer PAGADO = 1;
	private final Integer VENCIDO = 2;

	@Scheduled(cron = "0 58 20 * * *")
	public void calculoVencimientoCuota() throws ParseException {
		//Buscamos los detalles vencidos de los prestamos 
		List<PrestamoDetalle> prestamoDetalles = servicePrestamosDetalles.buscarPorEstado(2);
		if(!prestamoDetalles.isEmpty()) {
			for (PrestamoDetalle prestamoDetalle : prestamoDetalles) {
				//verificamos si pasaron los dias de gracia
				LocalDateTime fecha = convertToLocalDateTimeViaInstant(prestamoDetalle.getFechaGenerada());
				//Le sumamos los dias de gracia
				LocalDateTime fechaMax = fecha.plusDays(prestamoDetalle.getPrestamo().getDias_gracia());
				LocalDateTime fechaAcct =  LocalDateTime.now();
				if(fechaAcct.isAfter(fechaMax) || fechaAcct.isEqual(fechaMax)) {
					//valor_cuota x valor_interes_mora divido/30 x dias_vencidos
					double vencidos = diasVencidos(fecha, fechaAcct);
					//Se le suma 1 dia ya que se cuenta el mismo dia de vencimiento
//					vencidos+=1;
					//Calculamos los dias vencidos despues de los dias de gracia
					Double mora = ((prestamoDetalle.getCuota() * (prestamoDetalle.getInteres_mora()/100) ) / 30.00) * vencidos;
					prestamoDetalle.setMora(formato2d(mora));
					prestamoDetalle.setDias_atraso((int) vencidos);
					if(vencidos < 90) {
						prestamoDetalle.setEstado_cuota("Atraso");
					}else {
						prestamoDetalle.setEstado_cuota("Legal");
					}
					servicePrestamosDetalles.guardar(prestamoDetalle);
				}
			}
		}
	}
	
	@Scheduled(cron = "0 14 20 * * *")
	public void diasVencidos() throws ParseException {
		//Buscamos los detalles pendientes de los prestamos 
		List<PrestamoDetalle> prestamoDetalles = servicePrestamosDetalles.buscarPorEstado(0);
		if(!prestamoDetalles.isEmpty()) {
			for (PrestamoDetalle prestamoDetalle : prestamoDetalles) {
				//verificamos si pasaron los dias de gracia
				LocalDateTime fecha = convertToLocalDateTimeViaInstant(prestamoDetalle.getFechaGenerada());
				LocalDateTime fechaAcct =  LocalDateTime.now();
				if(fecha.isBefore(fechaAcct) || fecha.isEqual(fechaAcct)) {
					prestamoDetalle.setEstado(2);
					servicePrestamosDetalles.guardar(prestamoDetalle);
				}
			}
		}
	}
	
	@Scheduled(cron = "0 13 09 * * *")
	public void generarPrestamoInteresDetalle() throws ParseException {
		List<Prestamo> prestamosInteres = servicePrestamos.buscarPorTipo("2");
		for (Prestamo prestamo : prestamosInteres) {
			//verificamos la fecha del prestamo
			LocalDateTime fecha = convertToLocalDateTimeViaInstant(prestamo.getFecha());
			LocalDateTime fechaAcct =  LocalDateTime.now();
			double vencidos = diasVencidos(fecha, fechaAcct);	
			//Se le suma 1 dia ya que se cuenta el mismo dia de vencimiento
			vencidos+=1;
			for (int i = 0; i < vencidos; i++) {
				//Verificamos si tiene registro, sino tiene se genera
				LocalDateTime fechaTemp =  convertToLocalDateTimeViaInstant(prestamo.getFecha()).plusDays(i);
				Date tempDate = convertToDateViaInstant(fechaTemp);
				List<PrestamoInteresDetalle> temp = servicePrestamosInteresesDetalles.
						buscarPorPrestamoFecha(prestamo, tempDate);
				if(temp.isEmpty()) {
					Double interes = (prestamo.getBalance() * (prestamo.getTasa()/100.00) / 30);
					PrestamoInteresDetalle prestamoInteresDetalle = new PrestamoInteresDetalle();
					prestamoInteresDetalle.setCapital(prestamo.getBalance());
					prestamoInteresDetalle.setFecha(tempDate);
					prestamoInteresDetalle.setInteres(interes);
					prestamoInteresDetalle.setPrestamo(prestamo);
					servicePrestamosInteresesDetalles.guardar(prestamoInteresDetalle);
				}
			}
		}
	}
	
	@Scheduled(cron = "0 46 18 * * *")
	public void calculosPrestamosInteres() throws ParseException {
//		List<PrestamoInteresDetalle> prestamoInteresDetalles = servicePrestamosInteresesDetalles.buscarPorEstadoPagoYEstado(NORMAL, VENCIDO);
		List<PrestamoInteresDetalle> prestamoInteresDetalles = servicePrestamosInteresesDetalles.buscarPorEstado(NORMAL);
		LocalDateTime dateAcct =  LocalDateTime.now();
		LocalDateTime fechaVenciminto = null;
		for (PrestamoInteresDetalle prestamoInteresDetalle : prestamoInteresDetalles) {
			if(prestamoInteresDetalle.getVencimiento() == null) {
				//Calculamos la Fecha de vencimiento del interes
				LocalDateTime fecha = convertToLocalDateTimeViaInstant(prestamoInteresDetalle.getFecha());
				fechaVenciminto = fecha.plusDays(30 + prestamoInteresDetalle.getPrestamo().getDias_gracia());
				Date vencimiento = convertToDateViaInstant(fechaVenciminto);	
				prestamoInteresDetalle.setVencimiento(vencimiento);
				servicePrestamosInteresesDetalles.guardar(prestamoInteresDetalle);
			}else {
				fechaVenciminto = convertToLocalDateTimeViaInstant(prestamoInteresDetalle.getVencimiento());
			}
			
			//Verificamos si esta vencido
			if(dateAcct.isAfter(fechaVenciminto) || dateAcct.isEqual(fechaVenciminto)) {
				//valor_cuota x valor_interes_mora divido/30 x dias_vencidos
				double vencidos = diasVencidos(fechaVenciminto, dateAcct);
				//Se le suma 1 dia ya que se cuenta el mismo dia de vencimiento
				vencidos+=1;
				//Calculamos los dias vencidos despues de los dias de gracia
				
				prestamoInteresDetalle.setDias_atraso((int) vencidos);
				
				if(vencidos>0) {
					Double mora = ((prestamoInteresDetalle.getInteres() * (prestamoInteresDetalle.getPrestamo().getMora()/100) ) / 30.00) * vencidos;
					prestamoInteresDetalle.setMora(mora);
					prestamoInteresDetalle.setEstado(VENCIDO);
				}

				servicePrestamosInteresesDetalles.guardar(prestamoInteresDetalle);
			}
		}
	}
	
//	@Scheduled(cron = "0 28 18 * * *")
//	public void diasVencidosPrestamosInteres() throws ParseException {
//		LocalDateTime dateAcct =  LocalDateTime.now();
//		LocalDateTime fechaVenciminto = null;
//		List<PrestamoInteresDetalle> prestamoInteresDetalles = servicePrestamosInteresesDetalles.buscarPorEstado(NORMAL);
//		for (PrestamoInteresDetalle prestamoInteresDetalle : prestamoInteresDetalles) {
//			
//		}
//	}
	
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
