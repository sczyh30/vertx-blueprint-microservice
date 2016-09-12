require 'vertx/util/utils.rb'
# Generated from io.vertx.blueprint.microservice.store.StoreCRUDService
module VertxBlueprintStore
  #  A service interface for online store CURD operation.
  #  <p>
  #  This service is an event bus service (aka. service proxy).
  #  </p>
  class StoreCRUDService
    # @private
    # @param j_del [::VertxBlueprintStore::StoreCRUDService] the java delegate
    def initialize(j_del)
      @j_del = j_del
    end
    # @private
    # @return [::VertxBlueprintStore::StoreCRUDService] the underlying java delegate
    def j_del
      @j_del
    end
    #  Save an online store to the persistence layer. This is a so called `upsert` operation.
    #  This is used to update store info, or just apply for a new store.
    # @param [Hash] store store object
    # @yield async result handler
    # @return [void]
    def save_store(store=nil)
      if store.class == Hash && block_given?
        return @j_del.java_method(:saveStore, [Java::IoVertxBlueprintMicroserviceStore::Store.java_class,Java::IoVertxCore::Handler.java_class]).call(Java::IoVertxBlueprintMicroserviceStore::Store.new(::Vertx::Util::Utils.to_json_object(store)),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
      end
      raise ArgumentError, "Invalid arguments when calling save_store(store)"
    end
    #  Retrieve an online store by seller id.
    # @param [String] sellerId seller id, refers to an independent online store
    # @yield async result handler
    # @return [void]
    def retrieve_store(sellerId=nil)
      if sellerId.class == String && block_given?
        return @j_del.java_method(:retrieveStore, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(sellerId,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.toJson.encode) : nil : nil) }))
      end
      raise ArgumentError, "Invalid arguments when calling retrieve_store(sellerId)"
    end
    #  Remove an online store whose seller is <code>sellerId</code>.
    #  This is used to close an online store.
    # @param [String] sellerId seller id, refers to an independent online store
    # @yield async result handler
    # @return [void]
    def remove_store(sellerId=nil)
      if sellerId.class == String && block_given?
        return @j_del.java_method(:removeStore, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(sellerId,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
      end
      raise ArgumentError, "Invalid arguments when calling remove_store(sellerId)"
    end
  end
end
