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
package org.apache.ibatis.cache.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.CacheException;

/**
 * @author Clinton Begin
 */
// MyBatis的一级查询缓存（也叫作本地缓存）
// 是基于org.apache.ibatis.cache.impl.PerpetualCache 类的 HashMap本地缓存，
// 其作用域是SqlSession，myBatis 默认一级查询缓存是开启状态，且不能关闭。
// 在同一个SqlSession中两次执行相同的 sql查询语句，
// 第一次执行完毕后，会将查询结果写入到缓存中，
// 第二次会从缓存中直接获取数据，而不再到数据库中进行查询，
// 这样就减少了数据库的访问，从而提高查询效率。
// 基于PerpetualCache 的 HashMap本地缓存，其存储作用域为 Session，
// PerpetualCache 对象是在SqlSession中的Executor的localcache属性当中存放，
// 当 Session flush 或 close 之后，该Session中的所有 Cache 就将清空。

// - 二级缓存与一级缓存其机制相同，默认也是采用 PerpetualCache，HashMap存储，
// 不同在于其存储作用域为 Mapper(Namespace)，每个Mapper中有一个Cache对象，
// 存放在Configration中，并且将其放进当前Mapper的所有MappedStatement当中，
// 并且可自定义存储源，如 Ehcache。
//Mapper级别缓存，定义在Mapper文件的<cache>标签并需要开启此缓存
public class PerpetualCache implements Cache {

  private final String id;

  private final Map<Object, Object> cache = new HashMap<>();

  public PerpetualCache(String id) {
    this.id = id;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public int getSize() {
    return cache.size();
  }

  @Override
  public void putObject(Object key, Object value) {
    cache.put(key, value);
  }

  @Override
  public Object getObject(Object key) {
    return cache.get(key);
  }

  @Override
  public Object removeObject(Object key) {
    return cache.remove(key);
  }

  @Override
  public void clear() {
    cache.clear();
  }

  @Override
  public boolean equals(Object o) {
    if (getId() == null) {
      throw new CacheException("Cache instances require an ID.");
    }
    if (this == o) {
      return true;
    }
    if (!(o instanceof Cache)) {
      return false;
    }

    Cache otherCache = (Cache) o;
    return getId().equals(otherCache.getId());
  }

  @Override
  public int hashCode() {
    if (getId() == null) {
      throw new CacheException("Cache instances require an ID.");
    }
    return getId().hashCode();
  }

}
