/*
 *    Copyright 2009-2021 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.cache;

import java.util.concurrent.locks.ReadWriteLock;

/**
 * SPI for cache providers.
 * <p>
 * One instance of cache will be created for each namespace.
 * <p>
 * The cache implementation must have a constructor that receives the cache id as an String parameter.
 * <p>
 * MyBatis will pass the namespace as id to the constructor.
 *
 * <pre>
 * public MyCache(final String id) {
 *   if (id == null) {
 *     throw new IllegalArgumentException("Cache instances require an ID");
 *   }
 *   this.id = id;
 *   initialize();
 * }
 * </pre>
 *
 * @author Clinton Begin
 */

/**
 * ###  一级缓存
 *
 * - MyBatis的一级查询缓存（也叫作本地缓存）是基于org.apache.ibatis.cache.impl.PerpetualCache 类的 HashMap本地缓存，其作用域是SqlSession，myBatis 默认一级查询缓存是开启状态，且不能关闭。
 * - 在同一个SqlSession中两次执行相同的 sql查询语句，第一次执行完毕后，会将查询结果写入到缓存中，第二次会从缓存中直接获取数据，而不再到数据库中进行查询，这样就减少了数据库的访问，从而提高查询效率。
 * - 基于PerpetualCache 的 HashMap本地缓存，其存储作用域为 Session，PerpetualCache 对象是在SqlSession中的Executor的localcache属性当中存放，当 Session flush 或 close 之后，该Session中的所有 Cache 就将清空。
 *
 * ### 二级缓存
 *
 * - 二级缓存与一级缓存其机制相同，默认也是采用 PerpetualCache，HashMap存储，不同在于其存储作用域为 Mapper(Namespace)，每个Mapper中有一个Cache对象，存放在Configration中，并且将其放进当前Mapper的所有MappedStatement当中，并且可自定义存储源，如 Ehcache。
 *
 * - Mapper级别缓存，定义在Mapper文件的<cache>标签并需要开启此缓存
 */
public interface Cache {

  /**
   * @return The identifier of this cache
   */
  String getId();

  /**
   * @param key
   *          Can be any object but usually it is a {@link CacheKey}
   * @param value
   *          The result of a select.
   */
  void putObject(Object key, Object value);

  /**
   * @param key
   *          The key
   * @return The object stored in the cache.
   */
  Object getObject(Object key);

  /**
   * As of 3.3.0 this method is only called during a rollback
   * for any previous value that was missing in the cache.
   * This lets any blocking cache to release the lock that
   * may have previously put on the key.
   * A blocking cache puts a lock when a value is null
   * and releases it when the value is back again.
   * This way other threads will wait for the value to be
   * available instead of hitting the database.
   *
   *
   * @param key
   *          The key
   * @return Not used
   */
  Object removeObject(Object key);

  /**
   * Clears this cache instance.
   */
  void clear();

  /**
   * Optional. This method is not called by the core.
   *
   * @return The number of elements stored in the cache (not its capacity).
   */
  int getSize();

  /**
   * Optional. As of 3.2.6 this method is no longer called by the core.
   * <p>
   * Any locking needed by the cache must be provided internally by the cache provider.
   *
   * @return A ReadWriteLock
   */
  default ReadWriteLock getReadWriteLock() {
    return null;
  }

}
