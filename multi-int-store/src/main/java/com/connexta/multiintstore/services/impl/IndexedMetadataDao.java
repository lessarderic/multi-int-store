/*
 * Copyright (c) 2019 Connexta, LLC
 *
 * Released under the GNU Lesser General Public License version 3; see
 * https://www.gnu.org/licenses/lgpl-3.0.html
 */
package com.connexta.multiintstore.services.impl;

import com.connexta.multiintstore.common.StorageException;
import com.connexta.multiintstore.models.IndexedProductMetadata;
import com.connexta.multiintstore.repositories.IndexedMetadataRepository;
import com.connexta.multiintstore.services.api.Dao;
import com.connexta.multiintstore.services.api.DuplicateIdException;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class IndexedMetadataDao implements Dao<IndexedProductMetadata, String> {

  private final IndexedMetadataRepository repository;

  public IndexedMetadataDao(IndexedMetadataRepository repository) {
    this.repository = repository;
  }

  @Override
  public Optional<IndexedProductMetadata> getById(String id) throws StorageException {
    try {
      return repository.findById(id);
    } catch (RuntimeException e) {
      throw new StorageException("A problem occurred while using Solr", e);
    }
  }

  @Override
  public void save(IndexedProductMetadata indexedProductMetadata)
      throws StorageException, DuplicateIdException {
    Optional<IndexedProductMetadata> document = getById(indexedProductMetadata.getId());
    document.ifPresent(
        (metadata) -> {
          throw new DuplicateIdException(
              "Metadata with id " + metadata.getId() + " already exists");
        });
    try {
      repository.save(indexedProductMetadata);
    } catch (RuntimeException e) {
      throw new StorageException("A problem occurred while using Solr", e);
    }
  }
}
