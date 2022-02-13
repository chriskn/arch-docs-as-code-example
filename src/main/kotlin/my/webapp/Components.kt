package my.webapp

//@Component(description = "Responsible for the billing", technology = "Kotlin")
//class BillingService {
//    @UsesComponent(description = "Calculates invoice amount using")
//    val shoppingCard = ShoppingCard()
//
//    @UsesComponent(description = "Sends payment data to")
//    val paymentProvider = PaymentProvider()
//
//    @UsesComponent(description = "Gets user data from")
//    val userService = UserService()
//}
//
//@Component(description = "Provides access to available goods", technology = "Kotlin")
//class ShoppingCard {
//    @UsesComponent(description = "Determines good availability using")
//    val inventory = Inventory()
//}
//
//@Component(description = "Provides access to available goods", technology = "Kotlin")
//class Inventory
//
//@Component(description = "Implements user related logic", technology = "Kotlin")
//class UserService {
//    @UsesComponent(description = "Gets stored user data from")
//    val userRepository = UserRepository()
//}
//
//@Component(description = "Provides access to stored users", technology = "Kotlin, JPA")
//class UserRepository
//
//@Component(description = "Proxy for the payment provider", technology = "Kotlin, REST")
//class PaymentProvider