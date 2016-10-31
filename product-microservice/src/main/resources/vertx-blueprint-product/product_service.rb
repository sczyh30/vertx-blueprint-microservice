require 'vertx/util/utils.rb'
# Generated from io.vertx.blueprint.microservice.product.ProductService
module VertxBlueprintProduct
  #  A service interface managing products.
  #  <p>
  #  This service is an event bus service (aka. service proxy)
  #  </p>
  class ProductService
    # @private
    # @param j_del [::VertxBlueprintProduct::ProductService] the java delegate
    def initialize(j_del)
      @j_del = j_del
    end
    # @private
    # @return [::VertxBlueprintProduct::ProductService] the underlying java delegate
    def j_del
      @j_del
    end
    #  Initialize the persistence.
    # @yield the result handler will be called as soon as the initialization has been accomplished. The async result indicates whether the operation was successful or not.
    # @return [self]
    def initialize_persistence
      if block_given?
        @j_del.java_method(:initializePersistence, [Java::IoVertxCore::Handler.java_class]).call((Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling initialize_persistence()"
    end
    #  Add a product to the persistence.
    # @param [Hash] product a product entity that we want to add
    # @yield the result handler will be called as soon as the product has been added. The async result indicates whether the operation was successful or not.
    # @return [self]
    def add_product(product=nil)
      if product.class == Hash && block_given?
        @j_del.java_method(:addProduct, [Java::IoVertxBlueprintMicroserviceProduct::Product.java_class,Java::IoVertxCore::Handler.java_class]).call(Java::IoVertxBlueprintMicroserviceProduct::Product.new(::Vertx::Util::Utils.to_json_object(product)),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling add_product(product)"
    end
    #  Retrieve the product with certain `productId`.
    # @param [String] productId product id
    # @yield the result handler will be called as soon as the product has been retrieved. The async result indicates whether the operation was successful or not.
    # @return [self]
    def retrieve_product(productId=nil)
      if productId.class == String && block_given?
        @j_del.java_method(:retrieveProduct, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(productId,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.toJson.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling retrieve_product(productId)"
    end
    #  Retrieve the product price with certain `productId`.
    # @param [String] productId product id
    # @yield the result handler will be called as soon as the product has been retrieved. The async result indicates whether the operation was successful or not.
    # @return [self]
    def retrieve_product_price(productId=nil)
      if productId.class == String && block_given?
        @j_del.java_method(:retrieveProductPrice, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(productId,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling retrieve_product_price(productId)"
    end
    #  Retrieve all products.
    # @yield the result handler will be called as soon as the products have been retrieved. The async result indicates whether the operation was successful or not.
    # @return [self]
    def retrieve_all_products
      if block_given?
        @j_del.java_method(:retrieveAllProducts, [Java::IoVertxCore::Handler.java_class]).call((Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result.to_a.map { |elt| elt != nil ? JSON.parse(elt.toJson.encode) : nil } : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling retrieve_all_products()"
    end
    #  Retrieve products by page.
    # @param [Fixnum] page 
    # @yield the result handler will be called as soon as the products have been retrieved. The async result indicates whether the operation was successful or not.
    # @return [self]
    def retrieve_products_by_page(page=nil)
      if page.class == Fixnum && block_given?
        @j_del.java_method(:retrieveProductsByPage, [Java::int.java_class,Java::IoVertxCore::Handler.java_class]).call(page,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result.to_a.map { |elt| elt != nil ? JSON.parse(elt.toJson.encode) : nil } : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling retrieve_products_by_page(page)"
    end
    #  Delete a product from the persistence
    # @param [String] productId product id
    # @yield the result handler will be called as soon as the product has been removed. The async result indicates whether the operation was successful or not.
    # @return [self]
    def delete_product(productId=nil)
      if productId.class == String && block_given?
        @j_del.java_method(:deleteProduct, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(productId,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling delete_product(productId)"
    end
    #  Delete all products from the persistence
    # @yield the result handler will be called as soon as the products have been removed. The async result indicates whether the operation was successful or not.
    # @return [self]
    def delete_all_products
      if block_given?
        @j_del.java_method(:deleteAllProducts, [Java::IoVertxCore::Handler.java_class]).call((Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling delete_all_products()"
    end
  end
end
