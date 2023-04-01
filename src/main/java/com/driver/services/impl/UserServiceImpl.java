package com.driver.services.impl;

import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;
    @Autowired
    ServiceProviderRepository serviceProviderRepository3;
    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception{
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
        User user = new User(username,password);

        Country country = new Country();
        country.setCountryName(c);
        country.setCode(c.toCode());
        country.setUser(user);

        user.setCountry(country);
        user.setConnected(false);
        user.setOriginalIp(country.getCode()+"."+user.getId());
        user.setMaskedIp(null);

        return userRepository3.save(user);
    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {
        ServiceProvider serviceProvider = serviceProviderRepository3.findById(serviceProviderId).get();
        User user = userRepository3.findById(userId).get();

        user.getServiceProviderList().add(serviceProvider);
        serviceProvider.getUserList().add(user);

        return user;
    }
}
