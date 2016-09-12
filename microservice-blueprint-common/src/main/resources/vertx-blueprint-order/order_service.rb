require 'vertx/util/utils.rb'
# Generated from io.vertx.blueprint.microservice.order.OrderService
module VertxBlueprintOrder
  #  A service interface managing order storage operations.
  #  <p>
  #  This service is an event bus service (aka. service proxy).
  #  </p>
  class OrderService
    # @private
    # @param j_del [::VertxBlueprintOrder::OrderService] the java delegate
    def initialize(j_del)
      @j_del = j_del
    end
    # @private
    # @return [::VertxBlueprintOrder::OrderService] the underlying java delegate
    def j_del
      @j_del
    end
    #  Initialize the persistence.
    # @yield async result handler
    # @return [self]
    def initialize_persistence
      if block_given?
        @j_del.java_method(:initializePersistence, [Java::IoVertxCore::Handler.java_class]).call((Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling initialize_persistence()"
    end
    #  Retrieve orders belonging to a certain account.
    # @param [String] accountId account id
    # @yield async result handler
    # @return [self]
    def retrieve_orders_for_account(accountId=nil)
      if accountId.class == String && block_given?
        @j_del.java_method(:retrieveOrdersForAccount, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(accountId,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result.to_a.map { |elt| elt != nil ? JSON.parse(elt.toJson.encode) : nil } : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling retrieve_orders_for_account(accountId)"
    end
    #  Save an order into the persistence.
    # @param [Hash] order order data object
    # @yield async result handler
    # @return [self]
    def create_order(order=nil)
      if order.class == Hash && block_given?
        @j_del.java_method(:createOrder, [Java::IoVertxBlueprintMicroserviceOrder::Order.java_class,Java::IoVertxCore::Handler.java_class]).call(Java::IoVertxBlueprintMicroserviceOrder::Order.new(::Vertx::Util::Utils.to_json_object(order)),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling create_order(order)"
    end
    #  Retrieve the order with a certain <code>orderId</code>.
    # @param [Fixnum] orderId order id
    # @yield async result handler
    # @return [self]
    def retrieve_order(orderId=nil)
      if orderId.class == Fixnum && block_given?
        @j_del.java_method(:retrieveOrder, [Java::JavaLang::Long.java_class,Java::IoVertxCore::Handler.java_class]).call(orderId,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.toJson.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling retrieve_order(orderId)"
    end
  end
end
