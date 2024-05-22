package org.sau.devopsproject2.Controller;


import org.sau.devopsproject2.Config.AWSClientConfig;
import org.sau.devopsproject2.Entity.Person;
import org.sau.devopsproject2.Repository.PersonRepository;
import org.sau.devopsproject2.Service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private DocumentService documentService;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private AWSClientConfig awsClientConfig;
    @Autowired
    private PersonRepository personRepository;


    @GetMapping("/showFormForAdd")
    public String addPerson(Model model){

        Person person=new Person();

        model.addAttribute("person",person);

        return "add";

    }

    @PostMapping("/save")
    public String savePerson(@ModelAttribute Person person, @RequestParam("photo") MultipartFile photo, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            return "errorPage";
        }
        try {
            if (!photo.isEmpty()) {
                byte[] bytes = photo.getBytes();
                Path tempPath = Files.createTempFile("temp", photo.getOriginalFilename());
                Files.write(tempPath, bytes);

                String key = photo.getOriginalFilename(); // You can adjust the key as needed
                documentService.uploadFileToS3(awsClientConfig.getBucketName(), key, tempPath.toFile());

                person.setImg_url(documentService.generateImageUrl(awsClientConfig.getBucketName(), key));

                Files.deleteIfExists(tempPath);

            }
            personRepository.save(person);
            return "redirect:/person/list";
        }
        catch (IOException e) {
                e.printStackTrace();
                return "Failed to upload the file";  // Consider a more descriptive error handling
            }
    }



    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("personId")int personId,Model model){

        Optional<Person> person=personRepository.findById(personId);

        try {
            Person person1 = null;
            if (person.isPresent()) {
                person1 = person.get();
                String keyImg = extractFileName(person1.getImg_url());
                documentService.deleteFileFromS3(awsClientConfig.getBucketName(), keyImg);
            }
            model.addAttribute("person", person1);

            return "add";
        } catch (NullPointerException e){
            return e.getMessage();
        }

    }


    @GetMapping("/list")
    public String listPersons(Model model){

        List<Person>persons=personRepository.findAll();

        model.addAttribute("persons",persons);

        return "list";

    }


    @GetMapping("/delete")
    public String deletePerson(@RequestParam("personId") int personId) {
        Optional<Person> person = personRepository.findById(personId);
        try {
            if (person.isPresent()) {
                String key = extractFileName(person.get().getImg_url());
                personRepository.deleteById(person.get().getId());
                documentService.deleteFileFromS3(awsClientConfig.getBucketName(), key);
            }
            return "redirect:/person/list";
        } catch (NullPointerException e){
            return e.getMessage();
        }


    }
    public static String extractFileName(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            String path = url.getPath();
            // Assuming the path starts with a "/" and the file key is after the last "/"
            return path.substring(path.lastIndexOf('/') + 1);
        } catch (MalformedURLException e) {
            System.err.println("Error processing URL: " + imageUrl);
            e.printStackTrace();
            return null; // or handle more gracefully as needed
 }
}


}
