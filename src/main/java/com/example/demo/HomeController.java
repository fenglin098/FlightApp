package com.example.demo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.utils.ObjectUtils;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    FlightRepository flightRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/home")
    public String home(){
        return "home";
    }

    @RequestMapping("/")
    public String listFlights(Model model){
        model.addAttribute("flights", flightRepository.findAll());
        return "list";
    }

    @GetMapping("/add")
    public String flightForm(Model model){
        model.addAttribute("flight", new Flight());
        return "flightform";
    }

    @PostMapping("/process")
    public String processForm(@ModelAttribute Flight flight, @RequestParam("file") MultipartFile file){
        if (file.isEmpty()){
            flightRepository.save(flight);
            return "redirect:/";
        }
        try {
            Map uploadResult = cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype","auto"));
            flight.setPicture(uploadResult.get("url").toString());
            flightRepository.save(flight);
        }catch (IOException e){
            e.printStackTrace();
            return "redirect:/add";
        }return "redirect:/";

    }

    @RequestMapping("/detail/{id}")
    public String showFlight(@PathVariable("id") long id, Model model){
        model.addAttribute("flight", flightRepository.findById(id).get());
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String updateFlight(@PathVariable("id") long id, Model model){
        model.addAttribute("flight", flightRepository.findById(id).get());
        return "flightform";
    }

    @RequestMapping("delete/{id}")
    public String deleteMessage(@PathVariable("id") long id, Model model){
        flightRepository.deleteById(id);
        return "redirect:/";
    }

    @PostMapping("/result")
    public String search(Model model, @RequestParam("search") String search){
        model.addAttribute("flights", flightRepository.findByArrivingAirportContainingIgnoreCase(search));
        return "result";
    }
}
