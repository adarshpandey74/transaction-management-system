package com.adarsh.transaction_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;

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

    @GetMapping("/hello")
    @ResponseBody
    public String sayHello() {
        return "Hello Banking System 🚀";
    }

    @GetMapping("/hello-user")
    @ResponseBody
    public String hello(@RequestParam(required = false) String name) {
        if (name == null) {
            return "Please provide a name";
        }
        return "Hello " + name;
    }

    @GetMapping("/about")
    @ResponseBody
    public String about() {
        return "This is my first Spring Boot project";
    }

    @GetMapping("/success")
    @ResponseBody
    public String success() {
        return "User saved successfully 🎉";
    }

    @GetMapping("/edit-user")
    @ResponseBody
    public String editUser(@RequestParam Long id) {

        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return "User not found";
        }

        return "<html><body>" +
                "<h2>Edit User</h2>" +
                "<form action='/update-user' method='post'>" +
                "<input type='hidden' name='id' value='" + user.getId() + "' />" +
                "<input type='text' name='name' value='" + user.getName() + "' /><br/>" +
                "<input type='number' name='age' value='" + user.getAge() + "' /><br/>" +
                "<button type='submit'>Update</button>" +
                "</form>" +
                "</body></html>";
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

    @GetMapping("/delete-user")
    public String deleteUser(@RequestParam Long id) {

        userRepository.deleteById(id);

        return "redirect:/users";
    }

    @PostMapping("/hello-user")
    public String saveUser(@RequestParam String name,
                           @RequestParam int age) {

        User user = new User();
        user.setName(name);
        user.setAge(age);

        userRepository.save(user);

        return "redirect:/success";
    }
    @GetMapping("/users")
    @ResponseBody
    public String getUsers() {

        StringBuilder html = new StringBuilder();

        html.append("<html><body>");
        html.append("<h2>All Users</h2>");
        html.append("<table border='1' style='border-collapse: collapse;'>");
        html.append("<tr><th>ID</th><th>Name</th><th>Age</th><th>Action</th></tr>");

        userRepository.findAll().forEach(user -> {
            html.append("<tr>")
                    .append("<td>").append(user.getId()).append("</td>")
                    .append("<td>").append(user.getName()).append("</td>")
                    .append("<td>").append(user.getAge()).append("</td>")
                    .append("<td>")
                    .append("<a href='/edit-user?id=").append(user.getId()).append("'>Edit</a> ")
                    .append("<a href='/delete-user?id=").append(user.getId()).append("'>Delete</a>")
                    .append("</td>")
                    .append("</tr>");
        });

        html.append("</table>");
        html.append("</body></html>");

        return html.toString();
    }

    @PostMapping("/create-account")
    @ResponseBody
    public String createAccount(@RequestParam Long userId,
                                @RequestParam double balance) {

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return "User not found";
        }

        Account account = new Account();
        account.setUser(user);
        account.setBalance(balance);
        account.setAccountNumber("ACC" + userId);

        accountRepository.save(account);

        Transaction t = new Transaction();
        t.setType("CREATE");
        t.setAmount(balance);
        t.setDescription("Account created");
        t.setTimestamp(java.time.LocalDateTime.now());
        t.setAccount(account);

        transactionRepository.save(t);

        return "Account created for user " + user.getName();
    }

    @GetMapping("/accounts")
    @ResponseBody
    public String getAccounts() {

        StringBuilder html = new StringBuilder();

        html.append("<html><body>");
        html.append("<h2>All Accounts</h2>");
        html.append("<table border='1' style='border-collapse: collapse;'>");
        html.append("<tr><th>ID</th><th>Account No</th><th>Balance</th><th>User</th><th>Action</th></tr>");

        accountRepository.findAll().forEach(acc -> {
            html.append("<tr>")
                    .append("<td>").append(acc.getId()).append("</td>")
                    .append("<td>").append(acc.getAccountNumber()).append("</td>")
                    .append("<td>").append(acc.getBalance()).append("</td>")
                    .append("<td>").append(acc.getUser().getName()).append("</td>")
                    .append("<td>")
                    .append("<button onclick=\"location.href='/account-transactions?accountId=")
                    .append(acc.getId())
                    .append("'\">View</button>")
                    .append("</td>")
                    .append("</tr>");
        });

        html.append("</table>");
        html.append("</body></html>");

        return html.toString();
    }

    @PostMapping("/deposit")
    @ResponseBody
    public String deposit(@RequestParam Long accountId,
                          @RequestParam double amount) {

        Account account = accountRepository.findById(accountId).orElse(null);

        if (account == null) {
            return "Account not found";
        }

        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        Transaction t = new Transaction();
        t.setType("DEPOSIT");
        t.setAmount(amount);
        t.setDescription("Deposit to account");
        t.setTimestamp(java.time.LocalDateTime.now());
        t.setAccount(account);

        transactionRepository.save(t);

        return "Deposited " + amount + ". New Balance: " + account.getBalance();
    }

    @PostMapping("/withdraw")
    @ResponseBody
    public String withdraw(@RequestParam Long accountId,
                           @RequestParam double amount) {

        Account account = accountRepository.findById(accountId).orElse(null);

        if (account == null) {
            return "Account not found";
        }

        if (account.getBalance() < amount) {
            return "Insufficient balance ❌";
        }

        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);

        Transaction t = new Transaction();
        t.setType("WITHDRAW");
        t.setAmount(amount);
        t.setDescription("Withdraw from account");
        t.setTimestamp(java.time.LocalDateTime.now());
        t.setAccount(account);

        transactionRepository.save(t);

        return "Withdrawn " + amount + ". Remaining Balance: " + account.getBalance();
    }

    @PostMapping("/transfer")
    @ResponseBody
    public String transfer(@RequestParam Long fromAccountId,
                           @RequestParam Long toAccountId,
                           @RequestParam double amount) {

        Account from = accountRepository.findById(fromAccountId).orElse(null);
        Account to = accountRepository.findById(toAccountId).orElse(null);

        if (from == null || to == null) {
            return "One of the accounts not found";
        }

        if (from.getBalance() < amount) {
            return "Insufficient balance ❌";
        }

        // deduct from sender
        from.setBalance(from.getBalance() - amount);

        // add to receiver
        to.setBalance(to.getBalance() + amount);

        accountRepository.save(from);
        accountRepository.save(to);

        Transaction t1 = new Transaction();
        t1.setType("TRANSFER_OUT");
        t1.setAmount(amount);
        t1.setDescription("Sent money");
        t1.setTimestamp(java.time.LocalDateTime.now());
        t1.setAccount(from);

        transactionRepository.save(t1);

        Transaction t2 = new Transaction();
        t2.setType("TRANSFER_IN");
        t2.setAmount(amount);
        t2.setDescription("Received money");
        t2.setTimestamp(java.time.LocalDateTime.now());
        t2.setAccount(to);

        transactionRepository.save(t2);

        return "Transferred " + amount + " from Account " + fromAccountId +
                " to Account " + toAccountId;
    }

    @GetMapping("/transactions")
    @ResponseBody
    public String getTransactions() {

        StringBuilder html = new StringBuilder();

        html.append("<html><body>");
        html.append("<h2>All Transactions</h2>");
        html.append("<table border='1' style='border-collapse: collapse;'>");
        html.append("<tr><th>ID</th><th>Type</th><th>Amount</th><th>Account</th><th>Time</th></tr>");

        transactionRepository.findAll().forEach(t -> {
            html.append("<tr>")
                    .append("<td>").append(t.getId()).append("</td>")
                    .append("<td>").append(t.getType()).append("</td>")
                    .append("<td>").append(t.getAmount()).append("</td>")
                    .append("<td>").append(t.getAccount().getAccountNumber()).append("</td>")
                    .append("<td>").append(t.getTimestamp()).append("</td>")
                    .append("</tr>");
        });

        html.append("</table>");
        html.append("</body></html>");

        return html.toString();
    }

    @GetMapping("/account-transactions")
    @ResponseBody
    public String getAccountTransactions(@RequestParam Long accountId) {

        StringBuilder html = new StringBuilder();

        html.append("<html><body>");
        html.append("<h2>Transactions for Account ID: " + accountId + "</h2>");
        html.append("<table border='1'>");
        html.append("<tr><th>ID</th><th>Type</th><th>Amount</th><th>Time</th></tr>");

        transactionRepository.findAll().forEach(t -> {
            if (t.getAccount().getId().equals(accountId)) {
                html.append("<tr>")
                        .append("<td>").append(t.getId()).append("</td>")
                        .append("<td>").append(t.getType()).append("</td>")
                        .append("<td>").append(t.getAmount()).append("</td>")
                        .append("<td>").append(t.getTimestamp()).append("</td>")
                        .append("</tr>");
            }
        });

        html.append("</table>");
        html.append("</body></html>");

        return html.toString();
    }
}