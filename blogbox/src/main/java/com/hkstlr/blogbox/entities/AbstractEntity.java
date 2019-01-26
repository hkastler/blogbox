/*
 * Author henry.kastler hkstlr.com 2019
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hkstlr.blogbox.entities;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author henry.kastler
 */
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

    protected static final long serialVersionUID = 1L;
    
    /**
     *
     */
    @Id
    @Basic(optional = false)
    @NotNull(message = "{id.NotNull}")
    @Size(min = 1, max = 48)
    @Column(name = "id", nullable = false, length = 48)
    protected final String id;

    public AbstractEntity() {
        this.id = UUID.randomUUID().toString();
    }
    
    public String getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AbstractEntity)) {
            return false;
        }
        AbstractEntity other = (AbstractEntity) obj;
        return getId().equals(other.getId());
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + ":" + this.id;
    }
   
}
