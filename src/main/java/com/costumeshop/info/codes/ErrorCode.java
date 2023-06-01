package com.costumeshop.info.codes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public enum ErrorCode implements BasicMessageCode {
    ERR_001("Name is null or too long!"),
    ERR_002("Email is null or too long!"),
    ERR_003("Username is null or too long!"),
    ERR_004("Surname is null or too long!"),
    ERR_005("Password is null or too long!"),
    ERR_006("Phone is null or too long!"),
    ERR_007("User role is null!"),
    ERR_008("Error registering a new user: "),
    ERR_009("User with that email address already exists!"),
    ERR_010("User with that username address already exists!"),
    ERR_011("User with that username or email not found in the database!"),
    ERR_012("User account is not verified! Click on the verification link that has been mailed to you."),
    ERR_013("Authentication error! Is your email/username/password valid?"),
    ERR_014("User is null!"),
    ERR_015("User details are null! Problem with authentication!"),
    ERR_016("Verification token is null!"),
    ERR_017("User ID is null!"),
    ERR_018("User roles list is empty!"),
    ERR_019("Could not find user by verification token!"),
    ERR_020("User registration data is null!"),
    ERR_021("User address data is null"),
    ERR_022("City is null or too long!"),
    ERR_023("Postal code is null or too long!"),
    ERR_024("Flat number is null or too long!"),
    ERR_025("Error when encoding user's password!"),
    ERR_026("Cannot find information about user's email verification!"),
    ERR_027("Could not find user with ID: %1."),
    ERR_028("Error occurred when saving user during email verification: "),
    ERR_029("Failed to verify user with ID: %1"),
    ERR_030("Street is null or too long!"),
    ERR_031("Error occurred when logging in: "),
    ERR_032("Could not find user for the given username/email!"),
    ERR_033("Could not find user for this email!"),
    ERR_034("Error inserting new user into the database: "),
    ERR_035("Error occurred during verification token processing!"),
    ERR_036("Error occurred when adding address!"),
    ERR_037("Error occurred when retrieving addresses!"),
    ERR_038("Could not find address with ID: "),
    ERR_039("Error occurred when removing address! The address is connected to an existing complaint!"),
    ERR_040("Error occurred when removing address!"),
    ERR_041("Error occurred when changing password!"),
    ERR_042("Item image is null!"),
    ERR_043("Item image ID is null!"),
    ERR_044("Item image file is null!"),
    ERR_045("Item with image list is empty!"),
    ERR_046("Item is null!"),
    ERR_047("Item ID is null!"),
    ERR_048("Item description is null!"),
    ERR_049("Item price is null!"),
    ERR_050("Item title is null!"),
    ERR_051("Error occurred when retrieving items with images!"),
    ERR_052("Error occurred when retrieving item sizes!"),
    ERR_053("Could not find item with ID: %1."),
    ERR_054("Could not find item size with ID: %1."),
    ERR_055("User with ID: %1 failed to add items to his cart."),
    ERR_056("Failed to add item to cart!"),
    ERR_057("Failed to retrieve the cart for user with ID: %1."),
    ERR_058("Failed to retrieve items from cart!"),
    ERR_059("Order is null!"),
    ERR_060("Order status for order with ID: %1 is null!"),
    ERR_061("Order ID is null!"),
    ERR_062("Error occurred when retrieving all orders!"),
    ERR_063("Complaint is null!"),
    ERR_064("Complaint ID is null!"),
    ERR_065("Buyer user ID for complaint ID: %1 is null!"),
    ERR_066("Buyer name for complaint ID: %1 is null!"),
    ERR_067("Complaint status is null for complaint ID: %1."),
    ERR_068("Created date for complaint ID: %1 is null!."),
    ERR_069("Error occurred while retrieving all complaints!"),
    ERR_070("Could not find complaint with ID: %1."),
    ERR_071("Error occurred when retrieving complaint ID: %1."),
    ERR_072("Complaint chat ID is null!"),
    ERR_073("Complaint chat message text is null for chat message ID: %1!"),
    ERR_074("Complaint chat message created date is null for chat message ID: %1!"),
    ERR_075("Failed to retrieve complaint chat messages for complaint ID: %1."),
    ERR_076("Complaint chat message is null!"),
    ERR_077("Error occurred when saving the message for user ID: %1!"),
    ERR_078("Error occurred when assigning complaint to the employee!"),
    ERR_079("Error occurred when assigning complaint to the employee user ID: %1."),
    ERR_080("Failed to retrieve orders for user ID: %1."),
    ERR_081("Failed to retrieve order ID: %1."),
    ERR_082("Order details are null for order ID: %1."),
    ERR_083("Buyer user ID is null for order ID: %1."),
    ERR_084("Created date for order ID: %1 is null!"),
    ERR_085("Failed to retrieve order details for order ID: %1."),
    ERR_086("Failed to retrieve complaint status for status: %1."),
    ERR_087("Failed to retrieve complaint category for category: %1."),
    ERR_088("Error occurred when creating a new complaint!");

    private String message;

}