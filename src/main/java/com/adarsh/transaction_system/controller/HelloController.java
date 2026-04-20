package com.adarsh.transaction_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpSession;

import com.adarsh.transaction_system.entity.User;
import com.adarsh.transaction_system.repository.UserRepository;
import com.adarsh.transaction_system.repository.AccountRepository;
import com.adarsh.transaction_system.entity.Account;
import com.adarsh.transaction_system.repository.TransactionRepository;
import com.adarsh.transaction_system.entity.Transaction;

@Controller
public class HelloController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        org.springframework.ui.Model model) {

        // HARD CODED LOGIN (for now)
        if (username.equals("admin") && password.equals("1234")) {
            session.setAttribute("user", username);
            return "redirect:/";
        }

        model.addAttribute("error", "Invalid credentials ❌");
        return "login";
    }

    // HOME
    @GetMapping("/")
    public String home(org.springframework.ui.Model model,
                       HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        double totalBalance = accountRepository.findAll()
                .stream()
                .mapToDouble(Account::getBalance)
                .sum();

        long totalUsers = userRepository.count();
        long totalAccounts = accountRepository.count();

        var recentTransactions = transactionRepository.findAll()
                .stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .limit(5)
                .toList();

        model.addAttribute("totalBalance", totalBalance);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalAccounts", totalAccounts);
        model.addAttribute("recentTransactions", recentTransactions);

        return "index";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // ================= USER =================

    @PostMapping("/hello-user")
    public String saveUser(@RequestParam String name,
                           @RequestParam int age,
                           org.springframework.ui.Model model) {

        User user = new User();
        user.setName(name);
        user.setAge(age);

        userRepository.save(user);

        model.addAttribute("message", "User saved successfully 🎉");
        model.addAttribute("success", true);

        return "index";
    }

    @GetMapping("/users")
    public String getUsers(org.springframework.ui.Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "users";
    }

    @GetMapping("/delete-user")
    public String deleteUser(@RequestParam Long id) {
        userRepository.deleteById(id);
        return "redirect:/users";
    }

    @PostMapping("/update-user")
    public String updateUser(@RequestParam Long id,
                             @RequestParam String name,
                             @RequestParam int age) {

        User user = userRepository.findById(id).orElse(null);

        if (user != null) {
            user.setName(name);
            user.setAge(age);
            userRepository.save(user);
        }

        return "redirect:/users";
    }

    // ================= ACCOUNT =================

    @PostMapping("/create-account")
    public String createAccount(@RequestParam Long userId,
                                @RequestParam double balance,
                                org.springframework.ui.Model model) {

        if (balance < 0) {
            model.addAttribute("message", "Balance cannot be negative ❌");
            model.addAttribute("success", false);
            return "index";
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            model.addAttribute("message", "User not found ❌");
            model.addAttribute("success", false);
            return "index";
        }

        Account account = new Account();
        account.setUser(user);
        account.setBalance(balance);

        // Better account number
        account.setAccountNumber("ACC" + System.currentTimeMillis());

        accountRepository.save(account);

        model.addAttribute("message", "Account created successfully ✅");
        model.addAttribute("success", true);

        return "index";
    }

    @GetMapping("/accounts")
    public String getAccounts(org.springframework.ui.Model model) {
        model.addAttribute("accounts", accountRepository.findAll());
        return "accounts";
    }

    // ================= DEPOSIT =================

    @PostMapping("/deposit")
    public String deposit(@RequestParam Long accountId,
                          @RequestParam double amount,
                          org.springframework.ui.Model model) {

        if (amount <= 0) {
            model.addAttribute("message", "Invalid amount ❌");
            model.addAttribute("success", false);
            return "index";
        }

        Account account = accountRepository.findById(accountId).orElse(null);

        if (account == null) {
            model.addAttribute("message", "Account not found ❌");
            model.addAttribute("success", false);
            return "index";
        }

        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        model.addAttribute("message", "Deposit successful ✅");
        model.addAttribute("success", true);

        return "index";
    }

    // ================= WITHDRAW =================

    @PostMapping("/withdraw")
    public String withdraw(@RequestParam Long accountId,
                           @RequestParam double amount,
                           org.springframework.ui.Model model) {

        if (amount <= 0) {
            model.addAttribute("message", "Invalid amount ❌");
            model.addAttribute("success", false);
            return "index";
        }

        Account account = accountRepository.findById(accountId).orElse(null);

        if (account == null) {
            model.addAttribute("message", "Account not found ❌");
            model.addAttribute("success", false);
            return "index";
        }

        if (account.getBalance() < amount) {
            model.addAttribute("message", "Insufficient balance ❌");
            model.addAttribute("success", false);
            return "index";
        }

        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);

        Transaction t = new Transaction();
        t.setType("WITHDRAW");
        t.setAmount(amount);
        t.setDescription("Withdraw");
        t.setTimestamp(java.time.LocalDateTime.now());
        t.setAccount(account);

        transactionRepository.save(t);

        model.addAttribute("message", "Withdraw successful ✅");
        model.addAttribute("success", true);

        return "index";
    }

    // ================= TRANSFER =================

    @PostMapping("/transfer")
    public String transfer(@RequestParam Long fromAccountId,
                           @RequestParam Long toAccountId,
                           @RequestParam double amount,
                           org.springframework.ui.Model model) {

        if (amount <= 0) {
            model.addAttribute("message", "Invalid amount ❌");
            model.addAttribute("success", false);
            return "index";
        }

        Account from = accountRepository.findById(fromAccountId).orElse(null);
        Account to = accountRepository.findById(toAccountId).orElse(null);

        if (from == null || to == null) {
            model.addAttribute("message", "Account not found ❌");
            model.addAttribute("success", false);
            return "index";
        }

        if (from.getBalance() < amount) {
            model.addAttribute("message", "Insufficient balance ❌");
            model.addAttribute("success", false);
            return "index";
        }

        // Transfer
        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        accountRepository.save(from);
        accountRepository.save(to);

        // OUT transaction
        Transaction t1 = new Transaction();
        t1.setType("TRANSFER_OUT");
        t1.setAmount(amount);
        t1.setDescription("Sent money");
        t1.setTimestamp(java.time.LocalDateTime.now());
        t1.setAccount(from);

        transactionRepository.save(t1);

        // IN transaction
        Transaction t2 = new Transaction();
        t2.setType("TRANSFER_IN");
        t2.setAmount(amount);
        t2.setDescription("Received money");
        t2.setTimestamp(java.time.LocalDateTime.now());
        t2.setAccount(to);

        transactionRepository.save(t2);

        model.addAttribute("message", "Transfer successful ✅");
        model.addAttribute("success", true);

        return "index";
    }

    // ================= TRANSACTIONS =================

    @GetMapping("/transactions")
    public String getTransactions(org.springframework.ui.Model model) {
        model.addAttribute("transactions", transactionRepository.findAll());
        return "transactions";
    }

    @GetMapping("/account-transactions")
    public String getAccountTransactions(@RequestParam Long accountId,
                                         org.springframework.ui.Model model) {

        Account account = accountRepository.findById(accountId).orElse(null);

        if (account == null) {
            model.addAttribute("message", "Account not found ❌");
            model.addAttribute("success", false);
            return "index";
        }

        model.addAttribute("transactions",
                transactionRepository.findAll()
                        .stream()
                        .filter(t -> t.getAccount().getId().equals(accountId))
                        .toList());

        return "transactions";
    }
}