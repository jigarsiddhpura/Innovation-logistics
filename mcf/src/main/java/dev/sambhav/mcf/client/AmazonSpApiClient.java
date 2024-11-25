// package dev.sambhav.mcf.client;

// import com.amazon.spapi.client.ApiException;
// import com.amazon.spapi.client.ApiClient;
// import com.amazon.spapi.client.Configuration;
// import com.amazon.spapi.client.auth.AWSAuthenticationCredentials;
// import com.amazon.spapi.client.auth.AWSAuthenticationCredentialsProvider;
// import com.amazon.spapi.client.auth.LWAClientCredentials;
// import com.amazon.spapi.client.auth.SellingPartnerAPIAA;
// import com.amazon.spapi.api.OrdersApi;
// import com.amazon.spapi.model.orders.GetOrdersResponse;
// import com.amazon.spapi.model.orders.Order;

// import org.springframework.stereotype.Component;

// import java.util.List;
// import java.util.Arrays;

// @Component
// public class AmazonSpApiClient {

//     private final ApiClient apiClient;

//     public AmazonSpApiClient() {
//         // Assume environment variables or configuration properties are set for credentials
//         AWSAuthenticationCredentials awsAuthenticationCredentials = AWSAuthenticationCredentials.builder()
//                 .accessKeyId(System.getenv("AWS_ACCESS_KEY_ID"))
//                 .secretKey(System.getenv("AWS_SECRET_KEY"))
//                 .region("us-east-1") // Set your region
//                 .build();

//         AWSAuthenticationCredentialsProvider awsAuthenticationCredentialsProvider = AWSAuthenticationCredentialsProvider.builder()
//                 .roleArn(System.getenv("AWS_ROLE_ARN"))
//                 .roleSessionName("session-name")
//                 .build();

//         LWAClientCredentials lwaClientCredentials = LWAClientCredentials.builder()
//                 .clientId(System.getenv("LWA_CLIENT_ID"))
//                 .clientSecret(System.getenv("LWA_CLIENT_SECRET"))
//                 .refreshToken(System.getenv("LWA_REFRESH_TOKEN"))
//                 .build();

//         this.apiClient = new ApiClient()
//                 .awsAuthenticationCredentials(awsAuthenticationCredentials)
//                 .awsAuthenticationCredentialsProvider(awsAuthenticationCredentialsProvider)
//                 .lwaClientCredentials(lwaClientCredentials)
//                 .endpoint("https://sellingpartnerapi-na.amazon.com");

//         // Initialize the API client with authentication
//         apiClient.setAWSAuthenticationCredentials(awsAuthenticationCredentials);
//         apiClient.setAWSAuthenticationCredentialsProvider(awsAuthenticationCredentialsProvider);
//         apiClient.setLWAClientCredentials(lwaClientCredentials);
//     }

//     public List<Order> getOrderUpdates() throws ApiException {
//         OrdersApi ordersApi = new OrdersApi(apiClient);

//         String marketplaceIds = "ATVPDKIKX0DER"; // Marketplace ID for Amazon US
//         GetOrdersResponse response = ordersApi.getOrders(
//                 Arrays.asList(marketplaceIds), // marketplaceIds
//                 null, // createdAfter
//                 null, // createdBefore
//                 null, // lastUpdatedAfter
//                 null, // lastUpdatedBefore
//                 null, // orderStatuses
//                 null, // fulfillmentChannels
//                 null, // paymentMethods
//                 null, // buyerEmail
//                 null, // sellerOrderId
//                 null, // maxResultsPerPage
//                 null, // easyShipShipmentStatuses
//                 null, // nextToken
//                 null, // amazonOrderIds
//                 null, // actualFulfillmentSupplySourceId
//                 null, // isISPU
//                 null, // storeChainStoreId
//                 null  // dataElements
//         );

//         return response.getPayload().getOrders();
//     }
// }
