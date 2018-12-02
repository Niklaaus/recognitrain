package com.api.recognitrain.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrainingController {

	@CrossOrigin(origins = "http://localhost:4200")
	@PostMapping("/train/capture/")
	public ResponseEntity<?> storeImageForTraining(@RequestBody HashMap<String,String> req) {
		String imageBase64Encoded=req.get("image");
		String name=req.get("name");
		String directory = "/home/chilgoza/openface/training-images/"+name;
		File uploadedImage = null;
		BufferedOutputStream uploadStream = null;
		HashMap<String, String> resp = new HashMap<>();
		String filename = "uploaded";
		if (!imageBase64Encoded.isEmpty()) {
			try {
				int startOfBase64Data = imageBase64Encoded.indexOf(",") + 1;
				imageBase64Encoded = imageBase64Encoded.substring(startOfBase64Data, imageBase64Encoded.length());
				byte[] imageBytes = Base64.getDecoder().decode(imageBase64Encoded);
				///////////////////////////////////////////////////////////////
				// Creating the directory to store file/data/image ////////////
				File fileSaveDir = new File(directory);
				fileSaveDir.mkdirs();
				// Creates the save directory if it does not exists
				/*if (fileSaveDir.exists()) {
					resp.put("message", "Name already taken.");
					return new ResponseEntity(resp, HttpStatus.BAD_REQUEST);
				}*/
				filename = filename + "_" + (new Date()).toString().replaceAll("[^a-zA-Z0-9]", "_") + ".png";
				uploadedImage = new File(fileSaveDir.getAbsolutePath() + File.separator + filename);
				uploadStream = new BufferedOutputStream(new FileOutputStream(uploadedImage));
				uploadStream.write(imageBytes);

			//	resp.putAll(searchService.getPredictionResults(uploadedImage.getAbsolutePath()));

				return new ResponseEntity(HttpStatus.OK);
			} catch (Exception e) {
				//resp.put("message", "Image was corrupt");
				return new ResponseEntity(HttpStatus.BAD_REQUEST);
			} finally {
				if (uploadStream != null) {
					try {
						uploadStream.close();
					} catch (IOException e) {
						System.out.println("COULD NOT CLOSE BUFFERED STREAM");
						e.printStackTrace();
					}
				}
			}
		}

		else {
			resp.put("message", "Image was corrupt");
			return new ResponseEntity(resp, HttpStatus.BAD_REQUEST);
		}

	}

	@CrossOrigin(origins = "http://localhost:4200")
	@PostMapping("/train/askName/")
	public ResponseEntity<?> createFolderForTraining(@RequestBody String name) {
		
		HashMap<String, String> resp = new HashMap<>();
		try {
		File fileSaveDir = new File("/home/chilgoza/openface/training-images/"+name);
		if (fileSaveDir.exists()) {
			resp.put("message", "Name already taken. Please use another name");
			return new ResponseEntity(resp, HttpStatus.BAD_REQUEST);
		}
		fileSaveDir.mkdirs();
		// Creates the save directory if it does not exists
		}catch(Exception e) {
			e.printStackTrace();
			resp.put("message", "There is some error!! Kindly try another name or try again after sometime");
			return new ResponseEntity(resp,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		resp.put("message", "Store created successfully");
		return new ResponseEntity(resp,HttpStatus.OK);
	}
}
