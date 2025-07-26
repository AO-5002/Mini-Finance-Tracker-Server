package org.example.mini_finance_tracker_backend.controllers;

import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.example.mini_finance_tracker_backend.dtos.TransactionDto;
import org.example.mini_finance_tracker_backend.entities.TransactionEntity;
import org.example.mini_finance_tracker_backend.mappings.TransactionMapper;
import org.example.mini_finance_tracker_backend.repositories.TransactionRepository;
import org.example.mini_finance_tracker_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping(path = "/transaction/private", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(
        origins = "http://localhost:5173",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
public class TransactionController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private TransactionRepository transactionRepository; // Add this

    @PostMapping
    public ResponseEntity<TransactionDto> saveTransaction(@Valid @RequestBody TransactionDto transactionDto, Authentication auth) {

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
    public ResponseEntity<List<TransactionDto>> getTransactions(
            Authentication auth,
            @RequestParam(required = false, defaultValue = "date") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order
    ) {
        var auth0_id = auth.getName();
        var user =  userRepository.findByAuth0Id(auth0_id).orElseThrow(() -> new RuntimeException("User not found"));

        Comparator<TransactionEntity> comparator = switch (sortBy) {
            case "amount" -> Comparator.comparing(TransactionEntity::getAmount);
            case "date" -> Comparator.comparing(TransactionEntity::getCreatedAt);
            default -> Comparator.comparing(TransactionEntity::getCreatedAt); // fallback
        };

        if (order.equalsIgnoreCase("desc")) {
            comparator = comparator.reversed();
        }

        List<TransactionDto> listedItems = user.getTransactions().stream()
                .sorted(comparator)
                .map(transactionMapper::transactionToTransactionDto)
                .toList();
        return ResponseEntity.ok(listedItems);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TransactionDto> updateTransaction(@PathVariable String id, @RequestBody TransactionDto updatedItem, Authentication auth) throws BadRequestException {

        System.out.println("✅ PATCH called with ID: " + id);
        System.out.println("✅ Body received: " + updatedItem);
        // Get the Auth0 id of the user
        var auth0_id = auth.getName();
        var user = userRepository.findByAuth0Id(auth0_id).orElseThrow(() -> new RuntimeException("User not found"));
        UUID transactionUUID = UUID.fromString(id);
        var transactionID = transactionRepository.findById(transactionUUID).orElseThrow(()-> new RuntimeException("Transaction Not Found"));

        if (!transactionID.getUser().getUuid().equals(user.getUuid())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        else{

            // Safe null checks and updates
            if (updatedItem.getTransactionName() != null &&
                    !updatedItem.getTransactionName().equals(transactionID.getTransactionName())) {
                transactionID.setTransactionName(updatedItem.getTransactionName());
            }

            if (updatedItem.getType() != null &&
                    !updatedItem.getType().equals(transactionID.getType())) {
                transactionID.setType(updatedItem.getType());
            }

            if (updatedItem.getAmount() != null &&
                    !updatedItem.getAmount().equals(transactionID.getAmount())) {
                transactionID.setAmount(updatedItem.getAmount());
            }

            // Basically, we returned the transaction based on ID, and we are manually setting its fields to the new data. Finally, we save that new updated transaction

            transactionRepository.save(transactionID);
            return ResponseEntity.status(200).body(transactionMapper.transactionToTransactionDto(transactionID));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TransactionDto> deleteTransaction(@PathVariable String id, Authentication auth) {
        var auth0_id = auth.getName();
        var user = userRepository.findByAuth0Id(auth0_id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UUID transactionUUID = UUID.fromString(id);
        var delTransaction = transactionRepository.findById(transactionUUID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));

        // Optional: Check ownership
        if (!delTransaction.getUser().equals(user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access denied");
        }

        transactionRepository.delete(delTransaction);
        return ResponseEntity.ok(transactionMapper.transactionToTransactionDto(delTransaction));
    }






}
