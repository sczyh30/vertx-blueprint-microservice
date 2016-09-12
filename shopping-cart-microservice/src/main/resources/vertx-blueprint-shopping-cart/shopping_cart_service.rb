require 'vertx/util/utils.rb'
# Generated from io.vertx.blueprint.microservice.cart.ShoppingCartService
module VertxBlueprintShoppingCart
  module ShoppingCartService
    #  Add cart event to the event source.
    # @param [Hash] event cart event
    # @yield async result handler
    # @return [self]
    def add_cart_event(event=nil)
      if event.class == Hash && block_given?
        @j_del.java_method(:addCartEvent, [Java::IoVertxBlueprintMicroserviceCart::CartEvent.java_class,Java::IoVertxCore::Handler.java_class]).call(Java::IoVertxBlueprintMicroserviceCart::CartEvent.new(::Vertx::Util::Utils.to_json_object(event)),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling add_cart_event(event)"
    end
    #  Get shopping cart of a user.
    # @param [String] userId user id
    # @yield async result handler
    # @return [self]
    def get_shopping_cart(userId=nil)
      if userId.class == String && block_given?
        @j_del.java_method(:getShoppingCart, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(userId,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.toJson.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling get_shopping_cart(userId)"
    end
  end
  class ShoppingCartServiceImpl
    include ShoppingCartService
    # @private
    # @param j_del [::VertxBlueprintShoppingCart::ShoppingCartService] the java delegate
    def initialize(j_del)
      @j_del = j_del
    end
    # @private
    # @return [::VertxBlueprintShoppingCart::ShoppingCartService] the underlying java delegate
    def j_del
      @j_del
    end
  end
end
