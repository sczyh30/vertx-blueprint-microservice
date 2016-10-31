require 'vertx/util/utils.rb'
# Generated from io.vertx.blueprint.microservice.payment.PaymentQueryService
module VertxBlueprintPayment
  #  A service interface managing payment transactions query.
  #  <p>
  #  This service is an event bus service (aka. service proxy).
  #  </p>
  class PaymentQueryService
    # @private
    # @param j_del [::VertxBlueprintPayment::PaymentQueryService] the java delegate
    def initialize(j_del)
      @j_del = j_del
    end
    # @private
    # @return [::VertxBlueprintPayment::PaymentQueryService] the underlying java delegate
    def j_del
      @j_del
    end
    #  Initialize the persistence.
    # @yield the result handler will be called as soon as the initialization has been accomplished. The async result indicates whether the operation was successful or not.
    # @return [void]
    def initialize_persistence
      if block_given?
        return @j_del.java_method(:initializePersistence, [Java::IoVertxCore::Handler.java_class]).call((Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
      end
      raise ArgumentError, "Invalid arguments when calling initialize_persistence()"
    end
    #  Add a payment record into the backend persistence.
    # @param [Hash] payment payment entity
    # @yield async result handler
    # @return [void]
    def add_payment_record(payment=nil)
      if payment.class == Hash && block_given?
        return @j_del.java_method(:addPaymentRecord, [Java::IoVertxBlueprintMicroservicePayment::Payment.java_class,Java::IoVertxCore::Handler.java_class]).call(Java::IoVertxBlueprintMicroservicePayment::Payment.new(::Vertx::Util::Utils.to_json_object(payment)),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
      end
      raise ArgumentError, "Invalid arguments when calling add_payment_record(payment)"
    end
    #  Retrieve payment record from backend by payment id.
    # @param [String] payId payment id
    # @yield async result handler
    # @return [void]
    def retrieve_payment_record(payId=nil)
      if payId.class == String && block_given?
        return @j_del.java_method(:retrievePaymentRecord, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(payId,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.toJson.encode) : nil : nil) }))
      end
      raise ArgumentError, "Invalid arguments when calling retrieve_payment_record(payId)"
    end
  end
end
