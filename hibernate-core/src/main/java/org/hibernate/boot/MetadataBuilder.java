/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.boot;

import java.util.List;
import javax.persistence.SharedCacheMode;

import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.boot.archive.scan.spi.ScanEnvironment;
import org.hibernate.boot.archive.scan.spi.ScanOptions;
import org.hibernate.boot.archive.scan.spi.Scanner;
import org.hibernate.boot.archive.spi.ArchiveDescriptorFactory;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cfg.MetadataSourceType;
import org.hibernate.type.BasicType;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.UserType;

import org.jboss.jandex.IndexView;

/**
 * Contract for specifying various overrides to be used in metamodel building.
 *
 * @author Steve Ebersole
 * @author Hardy Ferentschik
 */
public interface MetadataBuilder {
	/**
	 * Specific the implicit schema name to apply to any unqualified database names
	 *
	 * @param implicitSchemaName The implicit schema name
	 *
	 * @return {@code this}, for method chaining
	 */
	public MetadataBuilder withImplicitSchemaName(String implicitSchemaName);

	/**
	 * Specific the implicit catalog name to apply to any unqualified database names
	 *
	 * @param implicitCatalogName The implicit catalog name
	 *
	 * @return {@code this}, for method chaining
	 */
	public MetadataBuilder withImplicitCatalogName(String implicitCatalogName);

	public MetadataBuilder with(ImplicitNamingStrategy namingStrategy);
	public MetadataBuilder with(PhysicalNamingStrategy namingStrategy);

	/**
	 * Defines the Hibernate Commons Annotations ReflectionManager to use
	 *
	 * @param reflectionManager The ReflectionManager to use.
	 *
	 * @return {@code this}, for method chaining
	 *
	 * @deprecated Deprecated (with no current replacement) to indicate that this will
	 * go away as we migrate away from Hibernate Commons Annotations to Jandex for annotation
	 * handling and XMl->annotation merging.
	 */
	@Deprecated
	public MetadataBuilder with(ReflectionManager reflectionManager);

	/**
	 * Specify the second-level cache mode to be used.  This is the cache mode in terms of whether or
	 * not to cache.
	 *
	 * @param cacheMode The cache mode.
	 *
	 * @return {@code this}, for method chaining
	 *
	 * @see #with(org.hibernate.cache.spi.access.AccessType)
	 */
	public MetadataBuilder with(SharedCacheMode cacheMode);

	/**
	 * Specify the second-level access-type to be used by default for entities and collections that define second-level
	 * caching, but do not specify a granular access-type.
	 *
	 * @param accessType The access-type to use as default.
	 *
	 * @return {@code this}, for method chaining
	 *
	 * @see #with(javax.persistence.SharedCacheMode)
	 */
	public MetadataBuilder with(AccessType accessType);

	/**
	 * Allows specifying a specific Jandex index to use for reading annotation information.
	 * <p/>
	 * It is <i>important</i> to understand that if a Jandex index is passed in, it is expected that
	 * this Jandex index already contains all entries for all classes.  No additional indexing will be
	 * done in this case.
	 *
	 * @param jandexView The Jandex index to use.
	 *
	 * @return {@code this}, for method chaining
	 */
	public MetadataBuilder with(IndexView jandexView);

	/**
	 * Specify the options to be used in performing scanning.
	 *
	 * @param scanOptions The scan options.
	 *
	 * @return {@code this}, for method chaining
	 */
	public MetadataBuilder with(ScanOptions scanOptions);

	/**
	 * Consider this temporary as discussed on {@link ScanEnvironment}
	 *
	 * @param scanEnvironment The environment for scanning
	 *
	 * @return {@code this}, for method chaining
	 */
	public MetadataBuilder with(ScanEnvironment scanEnvironment);

	/**
	 * Specify a particular Scanner instance to use.
	 *
	 * @param scanner The scanner to use.
	 *
	 * @return {@code this}, for method chaining
	 */
	public MetadataBuilder with(Scanner scanner);

	/**
	 * Specify a particular ArchiveDescriptorFactory instance to use in scanning.
	 *
	 * @param factory The ArchiveDescriptorFactory to use.
	 *
	 * @return {@code this}, for method chaining
	 */
	public MetadataBuilder with(ArchiveDescriptorFactory factory);

	/**
	 * Should the new (well "new" since 3.2) identifier generators be used for
	 * {@link javax.persistence.GenerationType#SEQUENCE},
	 * {@link javax.persistence.GenerationType#IDENTITY},
	 * {@link javax.persistence.GenerationType#TABLE} and
	 * {@link javax.persistence.GenerationType#AUTO} handling?
	 *
	 * @param enabled {@code true} says to use the new generator mappings; {@code false} says to use the legacy
	 * generator mappings.
	 *
	 * @return {@code this}, for method chaining
	 */
	public MetadataBuilder withNewIdentifierGeneratorsEnabled(boolean enabled);

	/**
	 * Should we process or ignore explicitly defined discriminators in the case
	 * of joined-subclasses.  The legacy behavior of Hibernate was to ignore the
	 * discriminator annotations because Hibernate (unlike some providers) does
	 * not need discriminators to determine the concrete type when it comes to
	 * joined inheritance.  However, for portability reasons we do now allow using
	 * explicit discriminators along with joined inheritance.  It is configurable
	 * though to support legacy apps.
	 *
	 * @param enabled Should processing (not ignoring) explicit discriminators be
	 * enabled?
	 *
	 * @return {@code this}, for method chaining
	 *
	 * @see org.hibernate.cfg.AvailableSettings#IGNORE_EXPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS
	 */
	public MetadataBuilder withExplicitDiscriminatorsForJoinedSubclassSupport(boolean enabled);

	/**
	 * Similarly to {@link #withExplicitDiscriminatorsForJoinedSubclassSupport},
	 * but here how should we treat joined inheritance when there is no explicitly
	 * defined discriminator annotations?  If enabled, we will handle joined
	 * inheritance with no explicit discriminator annotations by implicitly
	 * creating one (following the JPA implicit naming rules).
	 * <p/>
	 * Again the premise here is JPA portability, bearing in mind that some
	 * JPA provider need these discriminators.
	 *
	 * @param enabled Should we implicitly create discriminator for joined
	 * inheritance if one is not explicitly mentioned?
	 *
	 * @return {@code this}, for method chaining
	 *
	 * @see org.hibernate.cfg.AvailableSettings#IMPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS
	 */
	public MetadataBuilder withImplicitDiscriminatorsForJoinedSubclassSupport(boolean enabled);

	/**
	 * For entities which do not explicitly say, should we force discriminators into
	 * SQL selects?  The (historical) default is {@code false}
	 *
	 * @param supported {@code true} indicates we will force the discriminator into the select;
	 * {@code false} indicates we will not.
	 *
	 * @return {@code this}, for method chaining
	 *
	 * @see org.hibernate.cfg.AvailableSettings#FORCE_DISCRIMINATOR_IN_SELECTS_BY_DEFAULT
	 */
	public MetadataBuilder withImplicitForcingOfDiscriminatorsInSelect(boolean supported);

	/**
	 * Should nationalized variants of character data be used in the database types?
	 *
	 * For example, should {@code NVARCHAR} be used instead of {@code VARCHAR}?
	 * {@code NCLOB} instead of {@code CLOB}?
	 *
	 * @param enabled {@code true} says to use nationalized variants; {@code false}
	 * says to use the non-nationalized variants.
	 *
	 * @return {@code this}, for method chaining
	 *
	 * @see org.hibernate.cfg.AvailableSettings#USE_NATIONALIZED_CHARACTER_DATA
	 */
	public MetadataBuilder withNationalizedCharacterData(boolean enabled);

	/**
	 * Specify an additional or overridden basic type mapping.
	 *
	 * @param type The type addition or override.
	 *
	 * @return {@code this}, for method chaining
	 */
	public MetadataBuilder with(BasicType type);

	/**
	 * Register an additional or overridden custom type mapping.
	 *
	 * @param type The custom type
	 * @param keys The keys under which to register the custom type.
	 *
	 * @return {@code this}, for method chaining
	 */
	public MetadataBuilder with(UserType type, String[] keys);

	/**
	 * Register an additional or overridden composite custom type mapping.
	 *
	 * @param type The composite custom type
	 * @param keys The keys under which to register the composite custom type.
	 *
	 * @return {@code this}, for method chaining
	 */
	public MetadataBuilder with(CompositeUserType type, String[] keys);

	/**
	 * Apply an explicit TypeContributor (implicit application via ServiceLoader will still happen too)
	 *
	 * @param typeContributor The contributor to apply
	 *
	 * @return {@code this}, for method chaining
	 */
	public MetadataBuilder with(TypeContributor typeContributor);

	/**
	 * Apply a CacheRegionDefinition to be applied to an entity, collection or query while building the
	 * Metadata object.
	 *
	 * @param cacheRegionDefinition The cache region definition to apply
	 *
	 * @return {@code this}, for method chaining
	 */
	public MetadataBuilder with(CacheRegionDefinition cacheRegionDefinition);

	/**
	 * Apply a ClassLoader for use while building the Metadata.
	 * <p/>
	 * Ideally we should avoid accessing ClassLoaders when perform 1st phase of bootstrap.  This
	 * is a ClassLoader that can be used in cases when we have to.  IN EE managed environments, this
	 * is the ClassLoader mandated by
	 * {@link javax.persistence.spi.PersistenceUnitInfo#getNewTempClassLoader()}.  This ClassLoader
	 * is thrown away by the container afterwards.  The idea being that the Class can still be enhanced
	 * in the application ClassLoader.  In other environments, pass a ClassLoader that performs the
	 * same function if desired.
	 *
	 * @param tempClassLoader ClassLoader for use during building the Metadata
	 *
	 * @return {@code this}, for method chaining
	 */
	public MetadataBuilder with(ClassLoader tempClassLoader);

	public MetadataBuilder setSourceProcessOrdering(List<MetadataSourceType> ordering);

//	/**
//	 * Specify the resolve to be used in identifying the backing members of a
//	 * persistent attributes.
//	 *
//	 * @param resolver The resolver to use
//	 *
//	 * @return {@code this}, for method chaining
//	 */
//	public MetadataBuilder with(PersistentAttributeMemberResolver resolver);

	/**
	 * Actually build the metamodel
	 *
	 * @return The built metadata.
	 */
	public Metadata build();
}
