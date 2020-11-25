package com.contable.util;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class Utileria {
	
	public static String guardarArchivo(MultipartFile multiPart, String ruta) {  
		// Obtenemos el nombre original del archivo.
		String nombreOriginal = multiPart.getOriginalFilename();
		nombreOriginal = nombreOriginal.replace(" ", "-");
		String nombreFinal = randomAlphaNumeric(8) + nombreOriginal;
		try {
			// Formamos el nombre del archivo para guardarlo en el disco duro.
			File imageFile = new File(ruta+ nombreFinal);
			System.out.println("Archivo: " + imageFile.getAbsolutePath());
			//Guardamos fisicamente el archivo en HD.
			multiPart.transferTo(imageFile);
			return nombreFinal;
		} catch (IOException e) {
			System.out.println("Error " + e.getMessage());
			return null;
		}
	}
	
	public LinkedList<String> saveMultipleFiles(List<MultipartFile> files, String ruta) throws IOException {
		LinkedList<String> subidos = new LinkedList<>();
		for(MultipartFile multipart: files){
			String nombreOriginal = multipart.getOriginalFilename();
			nombreOriginal = nombreOriginal.replace(" ", "-");
			String nombreFinal = randomAlphaNumeric(8) + nombreOriginal;
			try {
				// Formamos el nombre del archivo para guardarlo en el disco duro.
				File imageFile = new File(ruta+ nombreFinal);
				System.out.println("Archivo: " + imageFile.getAbsolutePath());
				//Guardamos fisicamente el archivo en HD.
				multipart.transferTo(imageFile);
				subidos.add(nombreFinal);
			} catch (IOException e) {
				System.out.println("Error " + e.getMessage());
			}
        }
		return subidos;
    }
	
	/**
	 * Metodo para generar una cadena aleatoria de longitud N
	 * @param count
	 * @return
	 */
	public static String randomAlphaNumeric(int count) {
		String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder builder = new StringBuilder();
		while (count-- != 0) {
			int character = (int) (Math.random() * CARACTERES.length());
			builder.append(CARACTERES.charAt(character));
		}
		return builder.toString();
	}
	
}
