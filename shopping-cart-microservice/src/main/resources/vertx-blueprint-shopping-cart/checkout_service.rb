require 'vertx/vertx'
require 'vertx-service-discovery/service_discovery'
require 'vertx/util/utils.rb'
# Generated from io.vertx.blueprint.microservice.cart.CheckoutService
module VertxBlueprintShoppingCart
  #  A service interface for shopping cart checkout logic.
  #  <p>
  #  This service is an event bus service (aka. service proxy).
  #  </p>
  class CheckoutService
    # @private
    # @param j_del [::VertxBlueprintShoppingCart::CheckoutService] the java delegate
    def initialize(j_del)
      @j_del = j_del
    end
    # @private
    # @return [::VertxBlueprintShoppingCart::CheckoutService] the underlying java delegate
    def j_del
      @j_del
    end
    #  Create a shopping checkout service instance
    # @param [::Vertx::Vertx] vertx 
    # @param [::VertxServiceDiscovery::ServiceDiscovery] discovery 
    # @return [::VertxBlueprintShoppingCart::CheckoutService]
    def self.create_service(vertx=nil,discovery=nil)
      if vertx.class.method_defined?(:j_del) && discovery.class.method_defined?(:j_del) && !block_given?
        return ::Vertx::Util::Utils.safe_create(Java::IoVertxBlueprintMicroserviceCart::CheckoutService.java_method(:createService, [Java::IoVertxCore::Vertx.java_class,Java::IoVertxServicediscovery::ServiceDiscovery.java_class]).call(vertx.j_del,discovery.j_del),::VertxBlueprintShoppingCart::CheckoutService)
      end
      raise ArgumentError, "Invalid arguments when calling create_service(vertx,discovery)"
    end
    #  Shopping cart checkout.
    # @param [String] userId user id
    # @yield async result handler
    # @return [void]
    def checkout(userId=nil)
      if userId.class == String && block_given?
        return @j_del.java_method(:checkout, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(userId,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.toJson.encode) : nil : nil) }))
      end
      raise ArgumentError, "Invalid arguments when calling checkout(userId)"
    end
  end
end
