package org.example.mini_finance_tracker_backend.controllers;

import org.example.mini_finance_tracker_backend.dtos.TransactionDto;
import org.example.mini_finance_tracker_backend.mappings.TransactionMapper;
import org.example.mini_finance_tracker_backend.repositories.TransactionRepository;
import org.example.mini_finance_tracker_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = "/transaction/private", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(
        origins = "http://localhost:5173",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}
)
public class TransactionController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private TransactionRepository transactionRepository; // Add this

    @PostMapping
    public ResponseEntity<TransactionDto> saveTransaction(@RequestBody TransactionDto transactionDto, Authentication auth) {

        // Extract the auth0 id from the Auth Token
        var auth0_id = auth.getName();

        // Find the user via that Auth0 id; if not, throw an error
        var user = userRepository.findByAuth0Id(auth0_id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        var transactionEntity = transactionMapper.transactionDtoToTransaction(transactionDto);
        transactionEntity.setUser(user);
        transactionRepository.save(transactionEntity);

        return ResponseEntity.status(HttpStatus.CREATED).body(transactionDto);
    }

    @GetMapping
    public ResponseEntity<List<TransactionDto>> getTransactions(Authentication auth) {
        var auth0_id = auth.getName();
        var user =  userRepository.findByAuth0Id(auth0_id).orElseThrow(() -> new RuntimeException("User not found"));

        List<TransactionDto> listedItems = user.getTransactions().stream().map(transaction -> transactionMapper.transactionToTransactionDto(transaction)).toList();
        return ResponseEntity.ok(listedItems);
    }

    @DeleteMapping
    public ResponseEntity<TransactionDto> deleteTransaction(@RequestBody TransactionDto transactionDto, Authentication auth) {}





}
