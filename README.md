# Reward Web Api

This is a Spring Boot-based service designed to retrieve reward points for customers based on transactions stored in a PostgreSQL database.

## Technology Used
- **Java**: 8
- **Maven**: 3.9.9
- **Spring Boot**: v2.7.13
- **PostgreSQL**: 16

## API Endpoints

### Retrieve Rewards
- **URI**: `http://localhost:8082/api/rewards`
- **Method**: GET
- **Function**: This endpoint retrieves the reward points for customers from the PostgreSQL database.

#### Response
```json
{
  "CUST002": {
    "customerId": "CUST002",
    "monthlyPoints": 150,
    "totalPoints": 150
  },
  "CUST001": {
    "customerId": "CUST001",
    "monthlyPoints": 50,
    "totalPoints": 50
  }
}
