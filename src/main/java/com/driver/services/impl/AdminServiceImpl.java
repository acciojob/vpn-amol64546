package com.driver.services.impl;

import com.driver.model.Admin;
import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.repository.AdminRepository;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.String;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminRepository adminRepository1;

    @Autowired
    ServiceProviderRepository serviceProviderRepository1;

    @Autowired
    CountryRepository countryRepository1;

    @Override
    public Admin register(String username, String password) {
        Admin admin = new Admin(username,password);
        return adminRepository1.save(admin);
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) {
        Admin admin = adminRepository1.findById(adminId).get();
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setAdmin(admin);
        serviceProvider.setName(providerName);

        admin.getServiceProviderList().add(serviceProvider);

        return adminRepository1.save(admin);

    }

    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception{

        ServiceProvider serviceProvider = serviceProviderRepository1.findById(serviceProviderId).get();


        CountryName c;
        switch (countryName.toLowerCase()){
            case "ind":
                c = CountryName.IND;
                break;
            case "aus":
                c = CountryName.AUS;
                break;
            case "usa":
                c =  CountryName.USA;
                break;
            case "chi":
                c = CountryName.CHI;
                break;
            case "jpn":
                c = CountryName.JPN;
                break;
            default:
                throw new Exception("Country not found");
        }
        Country country = new Country();
        country.setCountryName(c);
        country.setCode(c.toCode());
        country.setServiceProvider(serviceProvider);

        serviceProvider.getCountryList().add(country);

        return serviceProviderRepository1.save(serviceProvider);
    }
}
