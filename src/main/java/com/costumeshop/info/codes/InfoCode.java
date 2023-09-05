package com.costumeshop.info.codes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public enum InfoCode implements BasicMessageCode {
    INFO_001("Did not find matching username: %1. Trying by email..."),
    INFO_002("Could not match neither username nor email: %2."),
    INFO_003("Registration successful."),
    INFO_004("Login successful. Hello, %1!"),
    INFO_005("User found using verification token."),
    INFO_006("User not found using verification token."),
    INFO_007("User account verified."),
    INFO_008("Addresses retrieved."),
    INFO_009("Address removed."),
    INFO_010("Password changed."),
    INFO_011("Items with images retrieved."),
    INFO_012("User logged. User ID: %1"),
    INFO_013("Address added."),
    INFO_014("Address with ID: %1 added for user with ID: %2"),
    INFO_015("Addresses retrieved for user with ID: %1"),
    INFO_016("Address removed. Address ID: %1"),
    INFO_017("New user with ID: %1 has registered."),
    INFO_018("User with ID: %1 has changed his password."),
    INFO_019("User with ID: %1 has been found using his verification token"),
    INFO_020("User with ID: %1 has verified his account."),
    INFO_021("Retrieved item sizes."),
    INFO_022("User with ID: %1 added to an existing cart item with ID: %2."),
    INFO_023("User with ID: %1 added items to a new cart item with ID: %2."),
    INFO_024("Item added to cart."),
    INFO_025("Cart retrieved successfully."),
    INFO_026("User with ID: %1 has retrieved his cart."),
    INFO_027("All orders retrieved."),
    INFO_028("All complaints retrieved."),
    INFO_029("Retrieved complaint ID: %1."),
    INFO_030("Complaint retrieved."),
    INFO_031("Complaint chat messages retrieved for complaint ID: %1."),
    INFO_032("Message sent."),
    INFO_033("User ID: %1 sent a chat message ID: %2"),
    INFO_034("Complaint assigned successfully."),
    INFO_035("Complaint ID: %1 has been assigned to employee user ID: %2."),
    INFO_036("Retrieved orders for user ID: %1."),
    INFO_037("Retrieved order details for order ID: %1."),
    INFO_038("New complaint created successfully."),
    INFO_039("New complaint created with ID: %1."),
    INFO_040("JWT token parsed correctly for username: %1."),
    INFO_041("User: %1 tries to access the endpoint: %2."),
    INFO_042("You do not have the authorization to access this resource."),
    INFO_043("All items retrieved by search text: %1. Found %2 items."),
    INFO_044("Items with images retrieved by search."),
    INFO_045("Cart items removed for user ID: %1."),
    INFO_046("Items removed from cart."),
    INFO_047("Cart item ID: %1 deleted for user ID: %2."),
    INFO_048("Item removed from cart."),
    INFO_049("New order has been created with ID: %1."),
    INFO_050("New order has been created."),
    INFO_051("New payment transaction has been created with ID: %1 for user ID: %2."),
    INFO_052("New payment transaction has been created."),
    INFO_053("Changed visibility for item with ID: %1."),
    INFO_054("Item visibility has been set."),
    INFO_055("New item has been inserted with ID: %1."),
    INFO_056("New item has been inserted."),
    INFO_057("Item categories retrieved."),
    INFO_058("Item sets retrieved."),
    INFO_059("Items retrieved by payment transaction ID: %1."),
    INFO_060("Payment transaction confirmation email has been sent to user ID: %1."),
    INFO_061("Complaint status has been updated to: %1 for complaint ID: %2."),
    INFO_062("All order statuses retrieved"),
    INFO_063("Order status has been updated for order ID: %1."),
    INFO_064("User retrieved by order ID: %1."),
    INFO_065("New item category has been inserted.");


    private String message;

}
